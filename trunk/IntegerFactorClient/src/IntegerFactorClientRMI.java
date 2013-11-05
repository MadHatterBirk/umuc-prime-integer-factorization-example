import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class IntegerFactorClientRMI extends JApplet implements ActionListener {
	private String yourServerIpAddress = ""; // "192.168.1.33";
	private int clientName;

	// IntegerFactorServer is the server for coordinating with the clients
	private IntegerFactorServerInterface IntegerFactorServer;
	// Create callback for use by the server to control the client
	private CallBackImpl callBackControl;

	// Buttons, status, and text area
	private JButton connectButton = new JButton("Connect to Server");
	private JButton disconnectButton = new JButton("Disconnect from Server");
	private JLabel jlblStatus = new JLabel("Click Connect!");
	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(textArea);

	boolean isStandalone = false;
	
	/** Does the prime factoring
	 * Will be modified based on methods from BigIntegerPrimeFactor.java
	 * 
	 * Testing BigIntegerPrimeFactor.java separately right now
	 * 
	 * @param number
	 */
	public void primeFactors(BigInteger number) {
		
		BigInteger numberToFactor = number;
		List<BigInteger> factors = new ArrayList<BigInteger>();
		textArea.append("Factoring: " + number + "\n");
		
		for (BigInteger i = BigInteger.valueOf(2); i.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
			while (number.remainder(i).compareTo(BigInteger.ZERO) == 0) {
				factors.add(i);
				textArea.append(i + "\n");
				number = number.divide(i);
			}
		}
		
		textArea.append("Finished Factoring!" + "\n");
		try {
			IntegerFactorServer.myResults(clientName, numberToFactor, factors);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Initialize the applet */
	public void init() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		connectButton.setActionCommand("connect");
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.setEnabled(false);

		buttonPanel.add(connectButton);
		buttonPanel.add(disconnectButton);

		textArea.setEditable(false);

		add(jlblStatus, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// Add action listener to button
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("connect".equals(e.getActionCommand())) {
				initializeRMI();
				
			} else if ("disconnect".equals(e.getActionCommand())) {
				textArea.setText("");
				connectButton.setEnabled(true);
				disconnectButton.setEnabled(false);
				IntegerFactorServer.disconnect(clientName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Initialize RMI */
	protected boolean initializeRMI() throws Exception {
		String host = yourServerIpAddress;
		if (!isStandalone)
			host = getCodeBase().getHost();
		try {
			Registry registry = LocateRegistry.getRegistry(host, 1099);
			IntegerFactorServer = (IntegerFactorServerInterface) registry
					.lookup("IntegerFactorServer");
			System.out.println("Server object " + IntegerFactorServer
					+ " found");
		} catch (Exception ex) {
			System.out.println(ex);
		}

		callBackControl = new CallBackImpl(this);
		if (IntegerFactorServer.connect((CallBack) callBackControl)) {
			System.out.println("Connected to server.");
			textArea.append("Connected to Server..." + "\n");
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			return true;
		} else {
			System.out.println("Connection Failed!");
			textArea.append("Connection Failed!" + "\n");
			return false;
		}
	}

	/** Set message on the status label */
	public void setMessage(String message) {
		jlblStatus.setText(message);
		textArea.append(message + "\n");
	}

	/** Main method */
	public static void main(String[] args) {
		IntegerFactorClientRMI applet = new IntegerFactorClientRMI();
		applet.isStandalone = true;
		applet.init();
		applet.start();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Integer Factor Client");
		frame.add(applet, BorderLayout.CENTER);
		frame.setSize(400, 320);
		frame.setVisible(true);
	}

	public void setName(int name) {
		clientName = name;
		
	}
	
}
