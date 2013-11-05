import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class IntegerFactorServer extends UnicastRemoteObject implements
		IntegerFactorServerInterface, ActionListener {

	private CallBack client = null;
	
	// list of clients connected
	private List<Client> clients = new ArrayList<Client>();
	private static List<BigInteger> list = new ArrayList<BigInteger>();
	private int index = 0;
	private List<BigInteger> results = new ArrayList<BigInteger>();
	private DefaultListModel<String> clientListModel; 

	// Buttons, status, and text area
	static JFrame frame = new JFrame();
	private JButton factorButton = new JButton("Factor");
	private JButton cancelButton = new JButton("Cancel");
	private JLabel jlblStatus = new JLabel("Server Waiting.");
	private JLabel factorLabel = new JLabel("Number to Factor:");
	private static JTextArea textArea = new JTextArea();
	private JTextField textField = new JTextField("2251797028667587");
	private JScrollPane scrollPane = new JScrollPane(textArea);

	boolean isStandalone = false;

	/** Constructs IntegerFactorServer object and exports it on default port. */
	public IntegerFactorServer() throws RemoteException {
		super();
	}

	/**
	 * Constructs IntegerFactorServer object and exports it on specified port.
	 * 
	 * @param port
	 *            The port for exporting
	 */
	public IntegerFactorServer(int port) throws RemoteException {
		super(port);
	}

	/**
	 * Connect to the IntegerFactor server.
	 */
	public boolean connect(CallBack clientID) throws RemoteException {		
		Client client = new Client((clients.size() + 1), clientID);
		client.clientID.setName(client.getClientName());
		client.clientID.notify("Connected to Server.\nClientID: " + client.getClientName() + "\nWaiting for other clients to connect...");
		clients.add(client);
		clientListModel.addElement("Node " + client.getClientName());	
		
		index++;
		System.out.println("Client " + client.getClientName() + " conected and Ready!");
		textArea.append("Client " + client.getClientName() + " conected and Ready!" + "\n");		
		
		return true;
	}

	public boolean disconnect(int clientID) throws RemoteException {
		Client client = clients.get(clientID-1);
		clients.remove(client);
		clientListModel.removeElement("Node " + client.getClientName());
		client.clientID.notify("Disconnected from Server");
		System.out.println("Client " + client.getClientName() + " disconected!");
		textArea.append("Client " + client.getClientName() + " disconected!" + "\n");
		return false;
	}

	/** A client invokes this method to notify the server of its results */
	public void myResults(int clientID, BigInteger number,
			List<BigInteger> resultList) throws RemoteException {
		
		Client client = clients.get(clientID-1);
		
		System.out.println("Client " + client.getClientName() + " finished Factoring!");
		textArea.append("Client " + client.getClientName() + " finished Factoring!" + "\n");
		
		results.addAll(resultList);
		
		System.out.println("Prime Factors of " + number);
		textArea.append("Prime Factors of " + number + "\n");

		for (BigInteger integer : resultList) {
			System.out.println(integer);
			textArea.append(integer + "\n");
		}
	}

	/** Initialize the applet */
	public void init() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
		    .addComponent(factorLabel)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        .addComponent(textField))
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        .addComponent(factorButton)
		        .addComponent(cancelButton))
		);
		layout.linkSize(SwingConstants.HORIZONTAL, factorButton, cancelButton);

		layout.setVerticalGroup(layout.createSequentialGroup()
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        .addComponent(factorLabel)
		        .addComponent(textField)
		        .addComponent(factorButton))
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        .addComponent(cancelButton))
		);		

		factorButton.setActionCommand("factor");
		cancelButton.setActionCommand("cancel");
		
		clientListModel = new DefaultListModel<String>();
		
		JList clientList = new JList(clientListModel); 
		clientList.setLayoutOrientation(JList.VERTICAL);
		clientList.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(clientList);
		listScroller.setPreferredSize(new Dimension(250, 80));

		panel.add(textField);
		panel.add(factorButton);
		panel.add(cancelButton);

		textArea.setEditable(false);

		frame.add(listScroller, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.SOUTH);

		// Add action listener to button
		factorButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("factor".equals(e.getActionCommand())) {
				list.add(new BigInteger(textField.getText()));
				sendClientJob();				
			} else if ("cancel".equals(e.getActionCommand())) {
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void sendClientJob(JTextField textField2) {
		for (int i = 0; i < list.size(); i++) {
			try {
				clients.get(0).clientID.factor(list.get(i));
				clients.get(1).clientID.factor(list.get(i+=1));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void sendClientJob() {
		for (int i = 0; i < list.size(); i++) {
			try {
				clients.get(0).clientID.factor(list.get(i));
				clients.get(1).clientID.factor(list.get(i++));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) {
		list.add(new BigInteger("864"));
		list.add(new BigInteger("99999990"));
		list.add(new BigInteger("2251797028667587"));
		try {
			IntegerFactorServer obj = new IntegerFactorServer();
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind("IntegerFactorServer", obj);

			obj.isStandalone = true;
			obj.init();

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("Integer Factor Server");

			frame.setSize(1000, 320);
			frame.setVisible(true);

			System.out.println("Server " + obj + " registered");
			textArea.append("Server " + obj + " registered" + "\n");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
