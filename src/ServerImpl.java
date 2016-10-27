import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 * @author Luiz Nunes Junior, Thiago Anders Imhoff
 */

public class ServerImpl implements Server {
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	
	public static void main(String[] args) throws RemoteException {
		connect();
	}
	
	private static void connect() throws RemoteException {
		try {
			socket = new Socket(InetAddress.getLocalHost(), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void execute() {}
	
	private void open() {}
	
	private void ls() {}
	
	private void cd() {}
	
	private void mv() {}
	
	private void mkdir() {}
	
	private void rmdir() {}
	
	private void rm() {}
	
	private void cp() {}
	
	private void close() {}
	
	private void cat() {}
	
	private void upload() {}
	
	private void download() {}
	
	private void lcd() {}
}
