import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 * @author Junior
 */

public class ServerImpl implements Server {
	static String host;
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	static InetAddress inet;
	
	public static void main(String[] args) {
		
		
		try {
			connect();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}  
	}
	
	public static void connect() throws RemoteException {
		 try {
			inet = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Connect with " + host);
			socket = new Socket(InetAddress.getByName(host), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			System.out.println( 
					"Error while connecting to " + host + ":" + port );
			System.out.println(e1.getMessage());
		}
	}
	
	public void open() {}
	
	public void ls() {}
	
	public void cd() {}
	
	public void mv() {}
	
	public void mkdir() {}
	
	public void rmdir() {}
	
	public void rm() {}
	
	public void cp() {}
	
	public void close() {}
	
	public void cat() {}
	
	public void upload() {}
	
	public void download() {}
	
	public void lcd() {}
}
