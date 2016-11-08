import java.io.ByteArrayOutputStream;
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
	static String host = "172.20.73.128";
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	static ByteArrayOutputStream output;
	static boolean greenlight; // is true when a connection is established
	
	public ClientImpl() {
		output = new ByteArrayOutputStream();
		greenlight = false;
	}

	public static void main(String[] args) throws RemoteException {
		try {
			StreamDetector sd = new StreamDetector();
			String message[] = null;
			
			for(;;) {
				System.out.println("(Test & Temp) Enter command: ");
				if(sd.detectInput() != null) {
					if(greenlight) {
						// the package is only built if there is an active connection
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.write(buildOutput(sd));
						// a single call of the write function is enough
						// because a byte array containing every field is given
						
						//TODO change message for a DataInputStream
						
					} else { System.out.println("Not connected to a server yet..."); }
					
					if(sd.getCommand() == 0) {
						// client has requested a connection
						String address = new String(sd.getArgument1(), "ASCII");
						if(connect(address)) { greenlight = true; }
					}
					
					// it doesn't make sense that the client is able to request commands
					// if he's not connected to a server yet
					
					
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
				System.out.println("(Test) Argument2 is NULL.)");
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
		}
		
		return output.toByteArray();
	}
	
}

/*
// out.write e out.writeBytes escrevem na impressora
// in.readByte() para ler um byte
// bufferedin.readLine() para ler uma linha
// insira o c�digo do cliente da impressora aqui
// consulte rfc1179.txt para o protocolo

DataInputStream in = new DataInputStream(socket
		.getInputStream());
DataOutputStream out = new DataOutputStream(socket
		.getOutputStream());
BufferedReader bufreader
  = new BufferedReader(new InputStreamReader(System.in))

out.writeByte(3);
out.writeBytes("RAW\n");
out.writeByte(2);
out.writeBytes("RAW\n");

if(in.readByte() == 0) {
	out.writeByte(2);
	out.writeBytes("31");
	out.writeBytes(" cfA001grad39\n");

	String data = "Hgrad29\nPTinhoso\nfdfa001grad39\n";

	out.writeBytes(data);
	out.writeByte(0);
	
	if(in.readByte() == 0) {
		out.writeByte(3);
		out.writeBytes("8");
		out.writeBytes(" dfA001grad39\n");
		
		String hello = "Hello39\n";
		
		out.writeBytes(hello);
		out.writeByte(0);
		
		System.out.println("HELLO");
		System.out.println(in.readByte());
	}
};

System.out.println("IN_READ_BYTE");
System.out.println(in.readByte());

in.close();
out.close();
socket.close();*/