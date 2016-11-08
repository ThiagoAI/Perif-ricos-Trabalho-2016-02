import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * @author Luiz Nunes Junior, Thiago Anders Imhoff 
 * @category Trabalho 1 de Interfaces e Periféricos
 * @since October 10th, 2016
 * 
 * Protocol StreamDetector by Kumamon, Tinhoso, FettBär, TotallyNotAllan
 * 1º Field: Which command? [Byte]
 * 2º Field: Argument size. [Byte]
 * 3º Field: Argument. [0 ~ 255 bytes]
 * (Traffic Only) 4º Field: File size size. [Byte]
 * (Traffic Only) 5º Field: File size. [0 ~ 255 bytes]
 * (Traffic Only) 6º Field: File. [File]
 */

public class ClientImpl {
	static int port = 1515;
	static int timeout = 30000; // 30s
	static Socket socket = null;
	static boolean greenlight = false; // is true when a connection is established
	static ByteArrayOutputStream output = null;
	static DataOutputStream out = null;
	static DataInputStream in = null;
	static StreamDetector sd = null;

	public static void main(String[] args) throws RemoteException {
		try {
			sd = new StreamDetector();
			
			for(;;) {
				System.out.print("(Temp) Enter command: ");
				if(sd.detectInput() != null) {
					if(greenlight) {
						// the package is only built if there is an active connection
						// otherwise we'll get an error regarding the socket
						
						out.write(buildOutput(sd));
						// a single call of the write function is enough
						// because a byte array containing every field is given
						
						in.wait();
						// the client must wait for an answer
						//TODO print message on screen
					}
					
					if(sd.getCommand() == 0) {
						// client has requested a connection
						String address = new String(sd.getArgument1(), "ASCII");
						if(greenlight == false) {
							// only tries to create a connection if there's no connection yet
							if(connect(address)) {
								// connection successfully established
								greenlight = true;
								out = new DataOutputStream(socket.getOutputStream());
								in = new DataInputStream(socket.getInputStream());
							}
						}
					}
					
					if(sd.getCommand() == 8) {
						// client desires to end the connection
						if(greenlight == true) {
							// only tries to end it if there's a connection
							greenlight = false;
							out.close();
							in.close();
							socket.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean connect(String address) throws RemoteException {
		try {
			System.out.println("Connect with " + address);
			socket = new Socket(InetAddress.getByName(address), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			System.out.println("Error while connecting to " + address + ":" + port);
			System.out.println(e1.getMessage());
			return false;
		}
		
		return true;
	}
	
	private static byte[] buildOutput(StreamDetector sd) {
		output = new ByteArrayOutputStream();
		
		try {
			output.write(sd.getCommand());
			if(sd.getArgument2() != null) {
				// if there is a second argument then write it
				output.write(sd.getSize1());
				output.write(sd.getArgument1());
				output.write(sd.getSize2());
				output.write(sd.getArgument2());
			} else {
				// otherwise just the first argument
				System.out.println("(Test) Argument2 is NULL.");
				output.write(sd.getSize1());
				output.write(sd.getArgument1());
			}
			
			if(sd.isOnlineCommand()) {
				// in case of cat, upload or download, we must write the file as well
				System.out.println("(Test) Command requires online connection.");
				output.write(sd.getFilesizeSize());
				output.write(sd.getFilesize());
				output.write(sd.getFile());
			}
		} catch(Exception e) {
			System.out.println("It was not possible to proceed with the request.");
			e.printStackTrace();
		}
		
		return output.toByteArray();
	}
	
}