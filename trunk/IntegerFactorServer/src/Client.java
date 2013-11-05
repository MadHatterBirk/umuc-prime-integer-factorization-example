import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Client {
	Integer clientName;
	CallBack clientID;
	BigInteger startRange;
	BigInteger endRange;
	List<BigInteger> ClientFactorsFound = new ArrayList<BigInteger>();
	long startTime, endTime;	
	
	public Client(int name, CallBack client) {
		clientName = name;
		clientID = client;
	}

	public BigInteger getStartRange() {
		return startRange;
	}

	public void setStartRange(BigInteger startRange) {
		this.startRange = startRange;
	}

	public BigInteger getEndRange() {
		return endRange;
	}

	public void setEndRange(BigInteger endRange) {
		this.endRange = endRange;
	}

	public List<BigInteger> getClientFactorsFound() {
		return ClientFactorsFound;
	}

	public void setClientFactorsFound(List<BigInteger> clientFactorsFound) {
		ClientFactorsFound = clientFactorsFound;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public Integer getClientName() {
		return clientName;
	}

	public CallBack getClientID() {
		return clientID;
	}	
	
	

}
