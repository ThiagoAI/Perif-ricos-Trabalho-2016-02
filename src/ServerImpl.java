import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
	static String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
	static ServerSocket server = null;
	static Socket client = null;
	static DataOutputStream out = null;
	static DataInputStream in = null;
	static ByteArrayOutputStream output = null;
	static byte arg1[] = null;
	static byte arg2[] = null;
	static byte file[] = null;
	static byte filesize[] = null;
	
	public static void main(String[] args) {
		try {
			System.out.println("Initializing...");
			server = new ServerSocket(port);
			client = server.accept();
			// the server will stop and wait for a successful connection
			// before proceeding
			System.out.println("Connected.");
			System.out.println(getCurrentPath());
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(;;) {
			// server only checks for an input if there's a connection
			try {
				byte command = in.readByte();
				execute(command);
				
				//out.notify();
				// awakes the client, indicating that there's a message for him
				// to print
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public static String getCurrentPath() {
		System.out.println("Hitler did nothing wrong.");
		return currentPath;
	}
	
	private static void unpack1() {
		try {
			byte size1 = in.readByte();
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void unpack2() {
		try {
			byte size1 = in.readByte();
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
			byte size2 = in.readByte();
			for(int iter = 0; iter < size2; iter++) { arg2[iter] = in.readByte(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void unpack3() {
		try {
			byte size1 = in.readByte();
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
			byte filesizesize = in.readByte();
			for(int iter = 0; iter < filesizesize; iter++) { filesize[iter] = in.readByte(); }
			//TODO must get file as byte array
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String toAsc2(byte transform[]) throws UnsupportedEncodingException {
		String answer = new String(transform, "ASCII");
		return answer;
	}
	
	public static String[] execute(byte command) throws UnsupportedEncodingException {
		String message[] = null;
		
		if(command == 1) {
			message = ls();
		}
		else if(command == 2) {
			unpack1();
			message = cd(toAsc2(arg1));
		}
		else if(command == 3) {
			unpack2();
			message = mv(toAsc2(arg1), toAsc2(arg2));
		}
		else if(command == 4) {
			unpack1();
			message = mkdir(toAsc2(arg1));
		}
		else if(command == 5) {
			unpack1();
			message = rmdir(toAsc2(arg1));
		}
		else if(command == 6) {
			unpack1();
			message = rm(toAsc2(arg1));
		}
		else if(command == 7) {
			unpack2();
			message = cp(toAsc2(arg1), toAsc2(arg2));
		}
		else if(command == 9) {
			unpack3();
			message = cat(toAsc2(arg1), file);
		}
		else if(command == 10) {
			unpack3();
			message = upload(toAsc2(arg1), file);
		}
		else if(command == 11) {
			unpack3();
			message = download(toAsc2(arg1), file);
		}
		else if(command == 12) {
			unpack1();
			message = lcd(toAsc2(arg1));
		}
		
		return message;
	}
	
	// obselete function
	// this fuctionality is done by the main
	/*
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
	*/
	
	//Transforma string no formato tamanho + string em bytes
	private static byte[] transform_to_byte(String string) throws UnsupportedEncodingException{
		byte[] temp = string.getBytes();
		byte size = (byte) temp.length;
		byte[] destination = new byte[size + 1];
		destination[0] = size;
		System.arraycopy(temp,0,destination,1,size);
		System.out.println("STUFF: " + destination);
		String x = new String(temp,"ASCII");
		System.out.println("LOL: " + x + "|" + size);
		return destination;
	}
	
	private static void buildOutput(byte[] array) {
		if(output == null) {
			System.out.println("(Test) output is null.");
			output = new ByteArrayOutputStream();
		}
		
		try {
			output.write(array);
		} catch(Exception e) {
			System.out.println("Something went wrong.");
			e.printStackTrace();
		}
	}
	
	//Childs tem todos os elementos no diretÃ³rio listados
	private static String[] ls() {
		File dir = new File(currentPath);
        String children[] = dir.list();
        
        for(int i = 0;i < children.length;i++){
        System.out.println(children[i]);
        }
        
        try {
        	System.out.println(children.length);
        	out.writeInt(children.length);
        	for(int iter = 0; iter < children.length; iter++) { buildOutput( transform_to_byte(children[iter]) ); }
        	System.out.println("kek " + output.size());
        	out.write(output.toByteArray());
        	output = null;
        } catch (Exception e) { e.printStackTrace(); }
        
        return children;
	}
	
	//Atualizamos o currentPath
	private static String[] cd(String directory) {
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
				currentPath = currentPath + "/" + directory;
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
	private static String[] mv(String source, String target){
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
	private static String[] mkdir(String directory) {
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
	private static String[] rmdir(String directory){
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
	private static String[] rm(String file) {
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
	private static String[] cp(String source, String target) {
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
	
	// needs to be remade
	/*private String[] close() {
		String message[] = {"[Success] Connection successfully closed."};
		try {
			socket.close();
		} catch (IOException e) {
			message[0] = "[Error] Connection shutdown has failed.";
		}
		return message;
	}*/
	
	private static String[] cat(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	private static String[] upload(String filename, byte[] file) {
		String message[] = {"[Success] Upload complete."};
		try {
			Files.write(Paths.get(filename), file);
		} catch (IOException e) {
			message[0] = "[Error] Upload incomplete.";
		}
		return message;
	}
	
	private static String[] download(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	// É UM CD FEITO NA MÁQUINA DO CLIENTE AO INVÉS DO SERVIDOR
	private static String[] lcd(String directory) {
		String message[] = {"Success"};
		return message;
	}
}
