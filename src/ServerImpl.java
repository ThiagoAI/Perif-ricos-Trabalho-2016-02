import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

/**
 * @author Luiz Nunes Junior, Thiago Anders Imhoff
 */

//TODO maybe it's not working properly given the absolute path

public class ServerImpl implements Server {
	static int port = 1515;
	static int timeout = 30000; // 30s
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
			server = new ServerSocket(port);
			System.out.println("Waiting for a connection request...");
			client = server.accept();
			
			// the server will stop and wait for a successful connection
			// before proceeding
			
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			
			// the server immediately send its current path to the client
			
			buildOutput( toByteArrayAlt(getCurrentPath()) );
			sendOutput();
			
			System.out.println("Connected.");
			
			for(;;) {
				// server only checks for an input if there's a connection
				byte command = in.readByte();
				execute(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getCurrentPath() {
		return currentPath;
	}
	
	private static void unpack1() {
		try {
			byte size1 = in.readByte();
			arg1 = new byte[size1];
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void unpack2() {
		try {
			byte size1 = in.readByte();
			arg1 = new byte[size1];
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
			byte size2 = in.readByte();
			arg2 = new byte[size2];
			for(int iter = 0; iter < size2; iter++) { arg2[iter] = in.readByte(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void unpack3() {
		try {
			byte size1 = in.readByte();
			arg1 = new byte[size1];
			for(int iter = 0; iter < size1; iter++) { arg1[iter] = in.readByte(); }
			
			byte filesizesize = in.readByte();
			filesize = new byte[filesizesize];
			for(int iter = 0; iter < filesizesize; iter++) { filesize[iter] = in.readByte(); }
			
			ByteBuffer wrapped = ByteBuffer.wrap(filesize);
			int realfilesize = wrapped.getInt();
			System.out.println("(Test) Real File Size: " + realfilesize);
			file = new byte[realfilesize];
			for(int iter = 0; iter < realfilesize; iter++) { file[iter] = in.readByte(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String toAsc2(byte transform[]) throws UnsupportedEncodingException {
		String answer = new String(transform, "ASCII");
		return answer;
	}
	
	public static void execute(byte command) throws UnsupportedEncodingException {
		if(command == 1) {
			ls();
		}
		else if(command == 2) {
			unpack1();
			cd(toAsc2(arg1));
		}
		else if(command == 3) {
			unpack2();
			mv(toAsc2(arg1), toAsc2(arg2));
		}
		else if(command == 4) {
			unpack1();
			mkdir(toAsc2(arg1));
		}
		else if(command == 5) {
			unpack1();
			rmdir(toAsc2(arg1));
		}
		else if(command == 6) {
			unpack1();
			rm(toAsc2(arg1));
		}
		else if(command == 7) {
			unpack2();
			cp(toAsc2(arg1), toAsc2(arg2));
		}
		else if(command == 9) {
			unpack3();
			cat(toAsc2(arg1), file);
		}
		else if(command == 10) {
			unpack3();
			upload(toAsc2(arg1), file);
		}
		else if(command == 11) {
			unpack3();
			download(toAsc2(arg1), file);
		}
		else if(command == 12) {
			unpack1();
			lcd(toAsc2(arg1));
		}
		
		StreamDetector.eraseArguments();
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
	
	// from string to a byte array where the first byte is the number of following bytes
	private static byte[] toByteArrayAlt(String string) {
		byte[] temp = string.getBytes();
		byte size = (byte) temp.length;
		byte[] destination = new byte[size + 1];
		destination[0] = size;
		System.arraycopy(temp,0,destination,1,size);
		System.out.println("KEK: " + string + "|" + size);
		return destination;
	}
	
	private static void operationStatus(boolean status) {
		byte[] array = new byte[1];
		
		if(status) { array[0] = (byte) 1; }
		else { array[0] = (byte) 0; }
		
		buildOutput(array);
	}
	
	private static void howManyOutputs(int size) {
		byte[] array = new byte[1];
		array[0] = (byte) size;
		buildOutput(array);
	}
	
	private static void buildOutput(byte[] array) {
		if(output == null) {
			output = new ByteArrayOutputStream();
		}
		
		try {
			output.write(array);
		} catch(Exception e) {
			System.out.println("Something went wrong.");
			e.printStackTrace();
		}
	}
	
	private static void sendOutput() {
		try {
			out.write(output.toByteArray());
			output = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Childs tem todos os elementos no diretÃ³rio listados
	private static void ls() {
		File dir = new File(currentPath);
        String children[] = dir.list();
        
        try {
        	operationStatus(true);
    		howManyOutputs(children.length);
        	for(int iter = 0; iter < children.length; iter++) { buildOutput( toByteArrayAlt(children[iter]) ); }
        } catch (Exception e) {
        	operationStatus(false);
        	buildOutput( toByteArrayAlt("ls : something went wrong.") );
        }
        
        sendOutput();
	}
	
	//Atualizamos o currentPath
	private static void cd(String directory) {
		//Se for .. voltamos para o diretÃ³rio pai
		if(directory.equals("..")) {
			File dir = new File(currentPath);
			String temp = dir.getParent();
			
			if(temp != null) {
				currentPath = temp;
			}
			
			operationStatus(true);
			buildOutput( toByteArrayAlt(getCurrentPath()) );
		} else {
			File dir = new File(currentPath + "/" + directory);
			
			if(dir.isDirectory() == true) {
				currentPath = currentPath + "/" + directory;
				operationStatus(true);
				buildOutput( toByteArrayAlt(getCurrentPath()) );
			} else {
				operationStatus(false);
	        	buildOutput( toByteArrayAlt("cd : no such file or directory.") );
			}
		}
		
		sendOutput();
	}
	
	//Tenta mover de source para target
	//Assumo que ela dÃ¡ os dois absolute path...
	//Se nÃ£o der tem que acrescentar currentPath
	private static void mv(String source, String target) {
		try {
			File src = new File(currentPath + "/" + source);
			
			if( src.renameTo(new File(currentPath + "/" + target + "/" + src.getName())) ){
				operationStatus(true);
			} else {
				operationStatus(false);
	        	buildOutput( toByteArrayAlt("mv : something went wrong.") );
			}
		}
		catch(Exception e) {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("mv : something went wrong.") );
		}
		
		sendOutput();
	}
	
	//Criamos diretÃ³rio
	private static void mkdir(String directory) {
		File dir = new File(currentPath + "/" + directory);
		
		if(dir.mkdir()) {
			operationStatus(true);
		}
		else {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("mkdir : something went wrong.") );
		}
		
		sendOutput();
	}
	
	//Destruimos o diretÃ³rio
	private static void rmdir(String directory) {
		File dir = new File(currentPath + "/" + directory);
		
		if(dir.delete()) {
			operationStatus(true);
		}
		else {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("rmdir : something went wrong.") );
		}

		sendOutput();
	}
	
	// delete files
	private static void rm(String file) {
		File dir = new File(currentPath + "/" + file);
		
		if(dir.delete()) {
			operationStatus(true);
		}
		else {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("rm : something went wrong.") );
		}

		sendOutput();
	}
	
	// copy and paste files
	private static void cp(String source, String target) {
		try {
			//isAbsolute
			File src = new File(currentPath + "/" + source);
			File tar = new File(currentPath + "/" + target + "/" + source);
			
			InputStream inStream = new FileInputStream(src);
	    	OutputStream outStream = new FileOutputStream(tar);
	    	
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	
	    	while( (length = inStream.read(buffer)) > 0) {
	    		outStream.write(buffer, 0, length);
	    	}
	    	
	    	inStream.close();
	    	outStream.close();
			
	    	operationStatus(true);
		}
		catch(Exception e) {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("cp : something went wrong.") );
		}

		sendOutput();
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
	
	private static void upload(String filename, byte[] file) {
		try {
			FileOutputStream fos = new FileOutputStream(currentPath + "/" + filename);
			fos.write(file);
			fos.close();
			
			operationStatus(true);
		} catch (Exception e) {
			operationStatus(false);
        	buildOutput( toByteArrayAlt("cp : something went wrong.") );
		}
		
		sendOutput();
	}
	
	private static String[] download(String filename, byte[] file) {
		String message[] = {"Success"};
		return message;
	}
	
	// cd local
	private static String[] lcd(String directory) {
		String message[] = {"Success"};
		return message;
	}
}
