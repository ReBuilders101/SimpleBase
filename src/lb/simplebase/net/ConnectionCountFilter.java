package lb.simplebase.net;

import java.util.function.BiPredicate;

public class ConnectionCountFilter implements BiPredicate<NetworkManagerServer, ConnectionInformation>{

	private int connectionLimit;
	
	public ConnectionCountFilter(int connectionLimit) {
		this.connectionLimit = connectionLimit;
	}
	
	@Override
	public boolean test(NetworkManagerServer var1, ConnectionInformation var2) {
		return var1.getCurrentClientCount() < connectionLimit;
	}
	
	public void setConnectionLimit(int newLimit) {
		connectionLimit = newLimit;
	}
	
	public int getConnectionLimit() {
		return connectionLimit;
	}

}
