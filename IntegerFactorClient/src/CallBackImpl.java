import java.math.BigInteger;
import java.rmi.*;
import java.rmi.server.*;

public class CallBackImpl extends UnicastRemoteObject implements CallBack {
	// The client will be called by the server through callback
	private IntegerFactorClientRMI thisClient;

	/** Constructor */
	public CallBackImpl(Object client) throws RemoteException {
		thisClient = (IntegerFactorClientRMI) client;
	}
	
	/** The Server Sets the Clients Name */
	public void setName(int clientName) throws RemoteException {
		thisClient.setName(clientName);
		
	}

	/** The server sends a message to be displayed by the client */
	public void notify(String message) throws RemoteException {
		thisClient.setMessage(message);
	}

	/** The server notifies a client of the number to factor */
	@Override
	public void factor(BigInteger number) throws RemoteException {
		thisClient.primeFactors(number);
		
	}

	
}