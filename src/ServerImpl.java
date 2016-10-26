import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 * @author Junior
 *
 * Atualmente, a estrutura do Stub é destinada ao cliente. Copiar o que está comentado para
 * ClientImpl.java.
 * E criar a interface Client.java.
 */

/*public static void main(String[] args) {
	try {
		ClientImpl serv = new ClientImpl();
		Client stub = (Client) UnicastRemoteObject.exportObject(serv, 2000);
		Registry registry = LocateRegistry.getRegistry();
		registry.bind("client", stub);
		System.err.println("> client has logged in");
	}
	catch (Exception e) {
	    System.err.println("@warning: client_exception: " + e.toString());  e.printStackTrace();
	}      
}*/

public class ServerImpl implements Server {
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
	
	public static void main(String[] args) {
		try {
			ServerImpl serv = new ServerImpl();
			Server objref = (Server) UnicastRemoteObject.exportObject(serv, 2000);
			Registry registry = LocateRegistry.getRegistry();
			final Client stub = (Client) registry.lookup("client");
			System.err.println("> server is up and running");
		}
		catch (Exception e) {
		    System.err.println("@warning: server_exception: " + e.toString());  e.printStackTrace();
		}      
	}
}
