import java.util.Scanner;
import java.util.Arrays;
import java.lang.Byte;

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
	 */
	private byte command;
	private byte[] sizes;
	private byte[] argument1;
	private byte[] argument2;
	
	//Auxiliar para funções, número de argumentos lidos na linha.
	private int numberOfArguments;
	/* Tipo de erro.
	 * 0 = comando não reconhecido.
	 * 1 = número errado de argumentos.
	 * 2 = argumento 1 inválido.
	 * 3 = argumento 2 inválido.
	 */
	private int errorType;
	
	public StreamDetector(){
		numberOfArguments = 0;
		command = 99;
		sizes = new byte[2];
	}
	
	//Detecta input e o retorna
	String detectInput(){
		Boolean condition = true;
		Scanner sc = new Scanner(System.in);
		String input = "";
		
		while(condition){
		//Começamos lendo.
		input = sc.nextLine();
		
		//Dividimos em um array de strings para analisarmos os argumentos separados
		String temp = input;
		int spaces = temp.length() - temp.replaceAll(" ", "").length();
		String arguments[] = input.split(" ",spaces+1);
		this.numberOfArguments = arguments.length;
		
		//Se entrar no if o commando é reconhecido
		if(checkAndSetCommand(arguments)){
			//Se não precisar pegar arquivo entra aqui
			if(this.command < 9 || this.command == 12){
				
						
			}
			//Se precisar de arquivo entra neste else
			else{
				
			}
			
		//Fim do if, o else só imprime erro pois o comando não é reconhecido.
		}
		else{
		System.out.println("Erro: comando nao reconhecido. Tente novamente.\n");
		}
		
		//Fim do while
		}
		
		sc.close();
		
		return input;
	}
	
	
	//Checa o input e seta o atributo command se estiver correto
	private Boolean checkAndSetCommand(String[] arguments){
		Boolean allright = true;
		String temp = arguments[0];
		
		if(temp.equals("open")){
			if(this.numberOfArguments != 2){
				this.errorType = 1;
			}
			else this.command = 0;
		}
		else if(temp.equals("ls")){
			this.command = 1;
		}
		else if(temp.equals("cd")){
			this.command = 2;
		}
		else if(temp.equals("mv")){
			this.command = 3;
		}
		else if(temp.equals("mkdir")){
			this.command = 4;
		}
		else if(temp.equals("rmdir")){
			this.command = 5;
		}
		else if(temp.equals("rm")){
			this.command = 6;
		}
		else if(temp.equals("cp")){
			this.command = 7;
		}
		else if(temp.equals("close")){
			this.command = 8;
		}
		else if(temp.equals("cat")){
			this.command = 9;
		}
		else if(temp.equals("upload")){
			this.command = 10;
		}
		else if(temp.equals("download")){
			this.command = 11;
		}
		else if(temp.equals("lcd")){
			this.command = 12;
		}
		else{
		    this.errorType = 0;
			allright = false;
		}
		
		return allright;
	}
	
	//Imprime erro baseado no errorType e então reseta ele para -1
	void printError(){
		if(this.errorType == 0){
			System.out.println("Erro " + this.errorType + "detectado: "
					+ "seu comando nao foi reconhecido.");
			return;
		}
		if(this.errorType == 1){
			System.out.println("Erro " + this.errorType + "detectado: "
					+ "o numero de argumentos para este comando nao esta correto.");
			return;
		}
		if(this.errorType == 2){
			System.out.println("Erro " + this.errorType + "detectado ");
			return;
		}
		if(this.errorType == 3){
			System.out.println("Erro " + this.errorType + "detectado ");
			return;
		}
		
		System.out.println("OPS! ALGO DE ERRADO ACONTECEU!"
					+ "Tipo do erro desconhecido: " + this.errorType);
		this.errorType = -1;
	}
	
	//GETTERS
	byte getCommand(){
		return this.command;
	}
	
	byte[] getSizes(){
		return this.sizes;
	}
	
	byte[] getArgument1(){
		return this.argument1;
	}
	
	byte[] getArgument2(){
		return this.argument2;
	}
	
}
