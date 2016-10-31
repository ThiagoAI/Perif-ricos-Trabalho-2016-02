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

//TODO NA TRATANDO CASO SE O USUÁRIO POR O CAMINHO COMPLETO
// SERÁ QUE É PRA TRATAR?

public class ServerImpl implements Server {
	static int port = 1515;
	static int timeout = 60000; // 60s
	static Socket socket = null;
	//Diretório atual do nosso programa
	//Luiz - Em java não se pode mudar o diretório de execução do programa
	//Por isso temos que guardar em qual diretório DEVERIAMOS estar
	//Para executarmos todas as operações com caminho absoluto
	String currentPath;
	
	//Um erro na classe pode ser uma boa
	//String error;
	
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
	private void execute() {}
	
	//esse aqui tu mexe
	private void open() {}
	
	//Childs tem todos os elementos no diretório listados
	private void ls() {
		File dir = new File(currentPath);
        String childs[] = dir.list();
        //return childs;
	}
	
	//Atualizamos o currentPath
	private void cd(String directory) {
		//Se for .. voltamos para o diretório pai
		if(directory.equals("..")){
			File dir = new File(currentPath);
			String temp = dir.getParent();
			if(temp != null){
				currentPath = temp;
			}
		}
		else{
			File dir = new File(currentPath + "/" + directory);
			if(dir.isDirectory()==true){
				this.currentPath = this.currentPath + "/" + directory;
			}
			else{
				//Comando está errado ver o que retornar aqui
				String error = directory + ": no such file or directory.";
			}
		}
		
	}
	
	//Tenta mover de source para target
	//Assumo que ela dá os dois absolute path...
	//Se não der tem que acrescentar currentPath
	private void mv(String source,String target){
		try{
			Path src = Paths.get(source);
			Path tar = Paths.get(target);
			Files.move(src,tar,StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e){
			String error = "There was an issue trying to...";
		}
	}
	
	//Criamos diretório
	private void mkdir(String directory) {
		File dir = new File(currentPath + "/" + directory);
		if(dir.mkdir()){
			//Nao fazer nada, teve sucesso
		}
		else{
			String error = "Falha ao tentar criar diretorio...";
		}
	}
	
	//Destruimos o diretório
	private void rmdir(String directory){
		File dir = new File(currentPath + "/" + directory);
		if(dir.delete()){
			//Nao fazer nada, teve sucesso
		}
		else{
			String error = "Falha ao tentar destruir diretorio...";
		}
	}
	
	private void rm() {}
	
	private void cp() {}
	
	private void close() {}
	
	private void cat() {}
	
	private void upload() {}
	
	private void download() {}
	
	private void lcd() {}
}
