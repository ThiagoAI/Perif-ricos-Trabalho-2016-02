import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * @author Luiz Nunes Junior, Thiago Anders Imhoff 
 * @category Trabalho 1 de Interfaces e PerifÃ©ricos
 * @since October 10th, 2016
 * 
 * Protocol StreamDetector by Kumamon, Tinhoso, FettBÃ¤r, TotallyNotAllan
 * 1Âº Field: Which command? [Byte]
 * 2Âº Field: Argument size. [Byte]
 * 3Âº Field: Argument. [0 ~ 255 bytes]
 * (Traffic Only) 4Âº Field: File size size. [Byte]
 * (Traffic Only) 5Âº Field: File size. [0 ~ 255 bytes]
 * (Traffic Only) 6Âº Field: File. [File]
 */

public class ClientImpl {
	static String host = "192.168.1.30";
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;

	public static void main(String[] args) throws RemoteException {
		//connect();
		
		// se socket não for nulo, a conexão está ok
		if(socket == null) {
			try {
				// responsável pela leitura e análise dos comandos
				StreamDetector sd = new StreamDetector();
				ServerImpl server = new ServerImpl();
				
				for(;;) {
					System.out.print("Be aware! This is a test! Your command: ");
					sd.detectInput();
					server.execute(sd);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private static void connect() throws RemoteException {
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
}

/*
// out.write e out.writeBytes escrevem na impressora
// in.readByte() para ler um byte
// bufferedin.readLine() para ler uma linha
// insira o código do cliente da impressora aqui
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