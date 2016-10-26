import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Luiz Nunes Junior, Thiago Anders Imhoff 
 * @category Trabalho 1 de Interfaces e Periféricos
 * @since October 10th, 2016
 * 
 * Protocol AssemblyWeb by Kumamon, Tinhoso, FettBär, TotallyNotAllan
 * 1º Field: Which command? [Byte]
 * 2º Field: Argument size. [Byte]
 * 3º Field: Argument. [0 ~ 255 bytes]
 * (Traffic Only) 4º Field: File size size. [Byte]
 * (Traffic Only) 5º Field: File size. [0 ~ 255 bytes]
 * (Traffic Only) 6º Field: File. [File]
 */

public class ClientImpl {

	static String host = "192.168.1.30"; /* Máquina do Thiago */
	static int port = 1515;
	static int timeout = 60000; /* 60s */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Socket socket = null;
		try {
			System.out.println("Connect with " + host);
			socket = new Socket(InetAddress.getByName(host), port);
			socket.setSoTimeout(timeout);

		} catch (Exception e1) {
			System.out
					.println("Error while connecting to " + host + ":" + port);
			System.out.println(e1.getMessage());
		}
		if (socket != null) {
			try {

				DataInputStream in = new DataInputStream(socket
						.getInputStream());
				DataOutputStream out = new DataOutputStream(socket
						.getOutputStream());
				
				BufferedReader bufferedin
		          = new BufferedReader(new InputStreamReader(in));
				
				/*out.writeByte(3);
				out.writeBytes("RAW\n");*/
				
				// use out.write e out.writeBytes para escrever para a impressora
				// in.readByte() para ler um byte
				// bufferedin.readLine() para ler uma linha
				/////// insira o seu c�digo do cliente da impressora aqui
				/////// consulte rfc1179.txt para o protocolo
				
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
				socket.close();

				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
