import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Paths;
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
	static String address = null;
	static String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
	static String serverCurrentPath = null;
	static ByteArrayOutputStream output = null;
	static DataOutputStream outs = null;
	static DataInputStream ins = null;
	static StreamDetector sd = null;

	public static void main(String[] args) {
		try {
			sd = new StreamDetector();
			
			for(;;) {
				if(greenlight) { System.out.print("Online @ " + serverCurrentPath + "$ "); }
				else { System.out.print("Offline @ " + currentPath + "$ "); }
				
				if(sd.detectInput() != null) {
					if(sd.getCommand() == 0) {
						// client has requested a connection
						address = new String(sd.getArgument1(), "ASCII");
						if(greenlight == false) {
							// only tries to create a connection if there's no connection yet
							if(connect(address)) {
								// connection successfully established
								greenlight = true;
								outs = new DataOutputStream(socket.getOutputStream());
								ins = new DataInputStream(socket.getInputStream());
								
								// the servers immediately writes its current path
								
								int size = ins.readByte();
								byte[] array = new byte[size];
								for(int iter = 0; iter < size; iter++) { array[iter] = ins.readByte(); }
								serverCurrentPath = new String(array, "ASCII");
							}
						} else { System.out.println("You are already connected to a server."); }
					} else if(greenlight) {
						// client is connected and has inserted a command
						if(sd.getCommand() == 8) {
							// client desires to end the connection
							greenlight = false;
							outs.close();
							ins.close();
							socket.close();
						} else {
							// another command was received
							// the package is only built if there is an active connection
							// otherwise we'll get an error regarding the socket
							
							outs.write(buildOutput(sd));
							// a single call of the write function is enough
							// because a byte array containing every field is given
							
							// the first byte always refers to the operation status
							if(ins.readByte() == 0) {
								// zero indicates an error
								int num = ins.readByte();
								byte[] array = new byte[num];
								for(int iter = 0; iter < num; iter++) { array[iter] = ins.readByte(); }
								String string = new String(array, "ASCII");
								System.out.println(string);
							} else {
								// otherwise, the operation was a success
								if(sd.getCommand() == 1) {
									// ls
									System.out.println("Operation Stats : Success.");
									int num = ins.readByte();
									for(int iter = 0; iter < num; iter++) {
										int size = ins.readByte();
										byte[] array = new byte[size];
										
										for(int ite = 0; ite < size; ite++) { array[ite] = ins.readByte(); }
										
										String string = new String(array, "ASCII");
										System.out.println(string);
									}
								} else if(sd.getCommand() == 2) {
									// cd
									int size = ins.readByte();
									byte[] array = new byte[size];
									for(int iter = 0; iter < size; iter++) { array[iter] = ins.readByte(); }
									serverCurrentPath = new String(array, "ASCII");
								}
							}
						}
					} else { System.out.println("This command requires online connection."); }
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
			} else if(sd.getArgument1() != null) {
				// otherwise just the first argument
				System.out.println("(Test) Argument2 is NULL.");
				output.write(sd.getSize1());
				output.write(sd.getArgument1());
			} else {
				System.out.println("(Test) Argument2 & Argument1 are NULL.");
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