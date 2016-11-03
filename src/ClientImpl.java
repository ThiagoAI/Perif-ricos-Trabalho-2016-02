import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
	static String host = "172.20.73.128";
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;

	public static void main(String[] args) throws RemoteException {
		//connect();
		
		// if socket is not null then we are online
		// requests from client are now green lighted
		if(socket == null) {
			try {
				StreamDetector sd = new StreamDetector();
				ServerImpl server = new ServerImpl();
				String message[] = null;
				
				for(;;) {
					System.out.print(server.getCurrentPath() + "$ ");
					// guarantees the integrity of our protocol
					// 172.20.73.128 - Server Thiago
					// 172.20.72.45 - Client Junior
					if(sd.detectInput() != null) {
						if(sd.getCommand() == 0) {
							String address = new String(sd.getArgument1(), "ASCII");
							System.out.println("Eu sou o Open-chan, e meu CPF � " + address + ".");
							connect(address);
						} else {
							message = server.execute(sd); // only approved 
							for(int iter = 0; iter < message.length; iter++) {
								if(message[iter] == "FlagEmptyDirectory") {
								} else { System.out.println(message[iter]); }
							}
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private static void connect(String address) throws RemoteException {
		try {
			System.out.println("Connect with " + address);
			socket = new Socket(InetAddress.getByName(address), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			System.out.println( 
					"Error while connecting to " + address + ":" + port );
			System.out.println(e1.getMessage());
		}
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