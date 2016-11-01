import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JTree;

/**
 * 
 * @author Luiz Nunes Junior, Thiago Anders Imhoff
 */

//TODO NA TRATANDO CASO SE O USUÃ�RIO POR O CAMINHO COMPLETO
// SERÃ� QUE Ã‰ PRA TRATAR?

public class ServerImpl implements Server {
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	String currentPath;
	
	//Um erro na classe pode ser uma boa
	//String error;
	
	public ServerImpl() {
		this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
	}
	
	public static void main(String[] args) throws RemoteException {
		connect();
	}
	
	public String getCurrentPath() {
		return this.currentPath;
	}
	
	private static void connect() throws RemoteException {
		try {
			socket = new Socket(InetAddress.getLocalHost(), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
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
		
		return message;
	}
	
	//esse aqui tu mexe
	private String[] open(String address) {
		String message[] = {"Connection has been established."};
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
		String message[] = {"flagnojump"};
		
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
		String message[] = {"Success."};
		try {
			File src = new File(currentPath + "/" + source);
			File tar = new File(target);
			Files.move(src.toPath(), tar.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e) {
			message[0] = "Error due to a non-existing file or directory.";
		}
		
		return message;
	}
	
	//Criamos diretÃ³rio
	private String[] mkdir(String directory) {
		String message[] = {"Success."};
		File dir = new File(currentPath + "/" + directory);
		if(dir.mkdir()){
			//Nao fazer nada, teve sucesso
		}
		else{
			message[0] = "Error.";
		}
		
		return message;
	}
	
	//Destruimos o diretÃ³rio
	private String[] rmdir(String directory){
		String message[] = {"Success."};
		File dir = new File(currentPath + "/" + directory);
		if(dir.delete()){
			//Nao fazer nada, teve sucesso
		}
		else{
			message[0] = "Error.";
		}
		
		return message;
	}
	
	private String[] rm(String file) {
		String message[] = {"Success."};
		File dir = new File(currentPath + "/" + file);
		if(dir.delete()){
			//eureka
		}
		else{
			message[0] = "Error.";
		}
		
		return message;
	}
	
	// TENHO QUE MELHORAR O TRATAMENTO DISSO CASO SEJA INTERNO A PASTA
	private String[] cp(String source, String target) {
		String message[] = {"Success."};
		try {
			File src = new File(currentPath + "/" + source);
			File tar = new File(target);
			Files.copy(src.toPath(), tar.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e){
			message[0] = "Error.";
		}
		
		return message;
	}
	
	private String[] close() {
		String message[] = {"Success"};
		return message;
	}
	
	private String[] cat(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	private String[] upload(String filename, byte[] file) {
		String message[] = {"Success"};
		try {
			Files.write(Paths.get(filename), file);
		} catch (IOException e) {
			message[0] = "Error due to connection lost or non-existing file.";
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
