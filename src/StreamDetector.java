import java.util.Scanner;

/* 
 * ls = 0
 * cd = 1
 * mv = 2
 * mkdir = 3
 * rmdir = 4
 * rm = 5
 * cp = 6
 * close = 7
 * cat = 8
 * upload = 9
 * download = 10
 * lcd = 11
 */

public class StreamDetector {
	private byte command;
	
	
	StreamDetector(){ };
	
	//Detecta input e o retorna
	String detectInput(){
		Boolean condition = true;
		Scanner sc = new Scanner(System.in);
		String input = "";
		
		while(condition){
		input = sc.nextLine();
		}
		
		sc.close();
		
		return input;
	}
	
	byte getCommand(){
		return this.command;
	}
	
}
