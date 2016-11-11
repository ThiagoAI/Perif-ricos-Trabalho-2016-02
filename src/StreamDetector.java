import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/* 
 * open = 0
 * ls = 1
 * cd = 2
 * mv = 3
 * mkdir = 4
 * rmdir = 5
 * rm = 6
 * cp = 7
 * close = 8
 * cat = 9
 * upload = 10
 * download = 11
 * lcd = 12
 */

public class StreamDetector {
	/* command = byte do comando, ver tabela acima
	 * sizes = array de tamanho 2 com o tamanho dos dois argumentos
	 * se só houver 1 o tamanho do segundo será 0
	 * argument1 e 2 = argumentos.
	 * filesize = tamanho da file passada como argumento
	 * file = arquivo lido em formato de byte array
	 */
	private byte command;
	private byte[] sizes;
	private byte[] argument1;
	private byte[] argument2;
	private byte[] filesize;
	private byte[] file;
	
	//Auxiliar para funções, número de argumentos lidos na linha.
	private int numberOfArguments;
	/* Tipo de erro.
	 * 0 = comando não reconhecido.
	 * 1 = número errado de argumentos.
	 * 2 = argumento 1 inválido.
	 * 3 = argumento 2 inválido.
	 */
	private int errorType;
	
	//Construtor
	public StreamDetector(){
		this.numberOfArguments = 0;
		this.command = 99;
		this.sizes = new byte[2];
		this.errorType = 99;
	}
	
	//Detecta input e o retorna
	String detectInput(){
		Boolean condition = true;
		Scanner sc = new Scanner(System.in);
		String input = "";
		String temp = "";
		
		while(condition){
		//Resetamos atributos
		this.numberOfArguments = 0;
		this.command = 99;
		this.errorType = 99;
		this.sizes[0] = 0;
		this.sizes[1] = 0;
		this.argument1 = null;
		this.argument2 = null;
		this.filesize = null;
		this.file = null;
			
		//Leitura
		input = sc.nextLine();
		
		//Dividimos em um array de strings para analisarmos os argumentos separados
		temp = input;
		int spaces = temp.length() - temp.replaceAll(" ", "").length();
		String arguments[] = input.split(" ",spaces+1);
		
		//Setamos número de argumentos
		this.numberOfArguments = arguments.length;
		
		//Para checarmos se o commando parece ok
		//Se tiver problema seta a variável errorType da classe
		checkAndSetCommand(arguments);
		
		//Se entrar no if é porque não houve erros. Se houver entra no else.
		if(this.errorType > 3 || this.errorType < 0){
				
				//Seta variaveis da classe
				if(this.numberOfArguments > 1){
				this.argument1 = arguments[1].getBytes();
				sizes[0] = (byte) argument1.length;
				}
				
				if(this.numberOfArguments > 2){
					this.argument2 = arguments[2].getBytes();
					sizes[1] = (byte) argument2.length;
				}
		
				//Quebramos o while. Se houver problema
				//durante a leitura de arquivo, volta a ser true depois
				condition = false;
				
				//Se for arquivo ainda temos de ver se ele existe
				//e pegar o tamanho
				if(this.command > 8 && this.command != 12){
					//Tentamos ler o arquivo
				
					try{
						//Abrimos o arquivo
						File file = new File(arguments[1]);
						
						//Pegamos o tamanho do arquivo
						long filesize = file.length();
						temp = String.valueOf(filesize);
						this.filesize = temp.getBytes();
						
						//Convertemos arquivo em bytes
						Path path = file.toPath();
						this.file = Files.readAllBytes(path);
					}
					//Se o arquivo não existir ou algum erro similar
					catch(Exception e){
						this.errorType = 2;
						condition = true;
						printError();
					}
					
				}
			
		}
		//Se tiver erro entra aqui
		else{
		printError();
		return null;
		/*this.command = 127;
		condition = false;*/
		}
		//Fim do while
		}
		//System.out.println("Command number " + this.command + ".");
		
		return input;
	}
	
	
	//Checa o input e seta o atributo command se estiver correto
	private void checkAndSetCommand(String[] arguments){
		String temp = arguments[0];
		
		if(temp.equals("open")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			else this.command = 0;
		}
		else if(temp.equals("ls")){
			if(this.numberOfArguments != 1){
				this.errorType = 1;
			}
			this.command = 1;
		}
		else if(temp.equals("cd")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 2;
		}
		else if(temp.equals("mv")){
			if(this.numberOfArguments != 3){
				this.errorType = 1;
			}
			this.command = 3;
		}
		else if(temp.equals("mkdir")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 4;
		}
		else if(temp.equals("rmdir")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 5;
		}
		else if(temp.equals("rm")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 6;
		}
		else if(temp.equals("cp")){
			if(this.numberOfArguments != 3){
				this.errorType = 1;
			}
			this.command = 7;
		}
		else if(temp.equals("close")){
			if(this.numberOfArguments != 1){
				this.errorType = 1;
			}
			this.command = 8;
		}
		else if(temp.equals("cat")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 9;
		}
		else if(temp.equals("upload")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 10;
		}
		else if(temp.equals("download")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 11;
		}
		else if(temp.equals("lcd")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			this.command = 12;
		}
		else{
		    this.errorType = 0;
		}
		
		this.eraseArguments();
	
	}
	
	//Imprime erro baseado no errorType e então reseta ele para -1
	void printError(){
		if(this.errorType == 0){
			System.out.println("Erro " + this.errorType + " detectado: "
					+ "seu comando nao foi reconhecido.");
			return;
		}
		if(this.errorType == 1){
			System.out.println("Erro " + this.errorType + " detectado: "
					+ "o numero de argumentos para este comando nao esta correto.");
			return;
		}
		if(this.errorType == 2){
			System.out.println("Erro " + this.errorType + " detectado: "
					+ "problema ao abrir arquivo, favor verificar se o caminho "
					+ "para o arquivo fornecido esta correto, ou se o arquivo "
					+ "existe.");
			return;
		}
		if(this.errorType == 3){
			System.out.println("Erro " + this.errorType + " detectado ");
			return;
		}
		
		System.out.println("OPS! ALGO DE ERRADO ACONTECEU!"
					+ "Tipo do erro desconhecido: " + this.errorType);
		this.errorType = -1;
	}
	
	public void eraseArguments() {
		this.argument1 = null;
		this.argument2 = null;
	}
	
	public boolean isOnlineCommand() {
		if(this.command == 9 || this.command == 10 || this.command == 11) {
			return true;
		}
		
		return false;
	}
	
	//GETTERS
	byte getCommand(){
		return this.command;
	}
	
	byte getSize1(){
		return this.sizes[0];
	}
	
	byte getSize2(){
		return this.sizes[1];
	}
	
	byte[] getArgument1(){
		return this.argument1;
	}
	
	byte[] getArgument2(){
		return this.argument2;
	}
	
	byte[] getFilesize(){
		return this.filesize;
	}
	
	byte getFilesizeSize() {
		return (byte) this.filesize.length;
	}
	
	byte[] getFile(){
		return this.file;
	}
	
}
