import java.io.File;
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
	
	private static void connect() throws RemoteException {
		try {
			socket = new Socket(InetAddress.getLocalHost(), port);
			socket.setSoTimeout(timeout);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	//wat
	// Luiz: Isso era pra testar se o byte tavam correspondendo certinho.
	public void execute(StreamDetector sd) {
		byte command = sd.getCommand();
		/*System.out.println(command);
		System.out.println(this.currentPath);*/
		System.out.println(sd.getArgument1());;
		if(command == 0) { this.open(sd.getArgument1().toString()); }
		else if(command == 1) { this.ls(); }
		else if(command == 2) { this.cd(sd.getArgument1().toString()); }
		else if(command == 3) { this.mv(sd.getArgument1().toString(), sd.getArgument2().toString()); }
		else if(command == 4) { this.mkdir(sd.getArgument1().toString()); }
		else if(command == 5) { this.rmdir(sd.getArgument1().toString()); }
		else if(command == 6) { this.rm(sd.getArgument1().toString()); }
		else if(command == 7) { this.cp(sd.getArgument1().toString(), sd.getArgument2().toString()); }
		else if(command == 8) { this.close(); }
		else if(command == 9) { this.cat(sd.getArgument1().toString()); }
		else if(command == 10) { this.upload(sd.getArgument1().toString()); }
		else if(command == 11) { this.download(sd.getArgument1().toString()); }
		else if(command == 12) { this.lcd(sd.getArgument1().toString()); }
	}
	
	//esse aqui tu mexe
	private void open(String address) {
		
	}
	
	//Childs tem todos os elementos no diretÃ³rio listados
	private void ls() {
		File dir = new File(currentPath);
        String children[] = dir.list();
        for(int iter = 0; iter < children.length; iter++) {
        	System.out.println(children[iter]);
        }
	}
	
	//Atualizamos o currentPath
	private void cd(String directory) {
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
				String error = directory + ": no such file or directory.";
			}
		}
		System.out.println(this.currentPath);
	}
	
	//Tenta mover de source para target
	//Assumo que ela dÃ¡ os dois absolute path...
	//Se nÃ£o der tem que acrescentar currentPath
	private void mv(String source, String target){
		try{
			Path src = Paths.get(source);
			Path tar = Paths.get(target);
			Files.move(src, tar, StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e){
			String error = "There was an issue trying to...";
		}
	}
	
	//Criamos diretÃ³rio
	private void mkdir(String directory) {
		File dir = new File(currentPath + "/" + directory);
		if(dir.mkdir()){
			//Nao fazer nada, teve sucesso
		}
		else{
			String error = "Falha ao tentar criar diretorio...";
		}
	}
	
	//Destruimos o diretÃ³rio
	private void rmdir(String directory){
		File dir = new File(currentPath + "/" + directory);
		if(dir.delete()){
			//Nao fazer nada, teve sucesso
		}
		else{
			String error = "Falha ao tentar destruir diretorio...";
		}
	}
	
	private void rm(String file) {}
	
	private void cp(String source, String target) {}
	
	private void close() {}
	
	private void cat(String file) {}
	
	private void upload(String file) {}
	
	private void download(String file) {}
	
	private void lcd(String directory) {}
}
