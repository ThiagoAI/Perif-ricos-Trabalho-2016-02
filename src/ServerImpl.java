import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Luiz Nunes Junior, Thiago Anders Imhoff
 */

//TODO maybe it's not working properly given the absolute path

public class ServerImpl implements Server {
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	String currentPath;
	static boolean greenlight; // equals to true when a connection is established
	
	public ServerImpl() {
		this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
		greenlight = false;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Initializing...");
		ServerSocket server = new ServerSocket(1515);
		Socket clientsocket = server.accept();
		System.out.println("Connected.");
		for(;;) {
			// server only checks for an input if there's a connection
			try {
				DataInputStream in = new DataInputStream(clientsocket.getInputStream());
				byte command = in.readByte();
				System.out.println("Reading byte by byte in order to test.");
				System.out.println("(Test) Command: " + command);
				byte arg1size = in.readByte();
				System.out.println("(Test) Arg 1 Size: " + arg1size);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getCurrentPath() {
		return this.currentPath;
	}
	
	public String[] execute(StreamDetector sd) throws UnsupportedEncodingException {
		byte command = sd.getCommand();
		String message[] = null;
		
		if(command == 0) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.open(arg1); 
		}
		else if(command == 1) {
			message = this.ls(); 
		}
		else if(command == 2) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.cd(arg1);
		}
		else if(command == 3) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			String arg2 = new String(sd.getArgument2(), "ASCII");
			message = this.mv(arg1, arg2);
		}
		else if(command == 4) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.mkdir(arg1);
		}
		else if(command == 5) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.rmdir(arg1);
		}
		else if(command == 6) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.rm(arg1);
		}
		else if(command == 7) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			String arg2 = new String(sd.getArgument2(), "ASCII");
			message = this.cp(arg1, arg2);
		}
		else if(command == 8) {
			message = this.close();
		}
		else if(command == 9) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.cat(arg1, sd.getFile());
		}
		else if(command == 10) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.upload(arg1, sd.getFile());
		}
		else if(command == 11) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.download(arg1, sd.getFile());
		}
		else if(command == 12) {
			String arg1 = new String(sd.getArgument1(), "ASCII");
			message = this.lcd(arg1);
		}
		
		sd.eraseArguments();
		// need to set both arguments to null in order to prevent
		// a bug that may happen if an one-argument command is
		// given after a two-arguments command
		
		return message;
	}
	
	// connect to the user
	private String[] open(String address) {
		String message[] = {"Connection has been established."};
		
		try {
			System.out.println("Connecting with " + address + "...");
			socket = new Socket(InetAddress.getByName(address), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e) {
			message[0] = "Error while connecting to " + address + " : " + port;
		}
		
		return message;
	}
	
	//Childs tem todos os elementos no diretÃ³rio listados
	private String[] ls() {
		File dir = new File(currentPath);
        String children[] = dir.list();
        return children;
	}
	
	//Atualizamos o currentPath
	private String[] cd(String directory) {
		String message[] = {"FlagEmptyDirectory"};
		
		//Se for .. voltamos para o diretÃ³rio pai
		if(directory.equals("..")) {
			File dir = new File(currentPath);
			String temp = dir.getParent();
			if(temp != null) {
				currentPath = temp;
			}
		} else {
			File dir = new File(currentPath + "/" + directory);
			if(dir.isDirectory() == true) {
				this.currentPath = this.currentPath + "/" + directory;
			}
			else {
				//Comando estÃ¡ errado ver o que retornar aqui
				message[0] = directory + ": no such file or directory.";
			}
		}
		
		return message;
	}
	
	//Tenta mover de source para target
	//Assumo que ela dÃ¡ os dois absolute path...
	//Se nÃ£o der tem que acrescentar currentPath
	private String[] mv(String source, String target){
		String message[] = {"[Success] File has been successfully moved."};
		try {
			File src = new File(currentPath + "/" + source);
			File tar = new File(target);
			Files.move(src.toPath(), tar.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e) {
			message[0] = "[Error] Could not move the file or directory.";
		}
		
		return message;
	}
	
	//Criamos diretÃ³rio
	private String[] mkdir(String directory) {
		String message[] = {"[Success] Folder created."};
		File dir = new File(currentPath + "/" + directory);
		if(dir.mkdir()){
			//Nao fazer nada, teve sucesso
		}
		else{
			message[0] = "[Error] Could not create the folder.";
		}
		
		return message;
	}
	
	//Destruimos o diretÃ³rio
	private String[] rmdir(String directory){
		String message[] = {"[Success] Folder deleted."};
		File dir = new File(currentPath + "/" + directory);
		if(dir.delete()){
			//Nao fazer nada, teve sucesso
		}
		else{
			message[0] = "[Error] Could not delete the folder.";
		}
		
		return message;
	}
	
	// delete files
	private String[] rm(String file) {
		String message[] = {"[Success] File deleted."};
		File dir = new File(currentPath + "/" + file);
		if(dir.delete()){
			//Success.
		}
		else{
			message[0] = "[Error] Could not delete the file.";
		}
		
		return message;
	}
	
	// copy and paste files
	private String[] cp(String source, String target) {
		String message[] = {"[Success] Copied and pasted."};
		try {
			File src = new File(currentPath + "/" + source);
			File tar = new File(target);
			Files.copy(src.toPath(), tar.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e){
			message[0] = "[Error] File could not be copied.";
		}
		
		return message;
	}
	
	private String[] close() {
		String message[] = {"[Success] Connection successfully closed."};
		try {
			socket.close();
		} catch (IOException e) {
			message[0] = "[Error] Connection shutdown has failed.";
		}
		return message;
	}
	
	private String[] cat(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	private String[] upload(String filename, byte[] file) {
		String message[] = {"[Success] Upload complete."};
		try {
			Files.write(Paths.get(filename), file);
		} catch (IOException e) {
			message[0] = "[Error] Upload incomplete.";
		}
		return message;
	}
	
	private String[] download(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	// É UM CD FEITO NA MÁQUINA DO CLIENTE AO INVÉS DO SERVIDOR
	private String[] lcd(String directory) {
		String message[] = {"Success"};
		return message;
	}
}
