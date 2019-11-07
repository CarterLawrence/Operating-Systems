import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Seq {
	
	//ACTGACGCAG,TCACAACGGG,GAGTCCAGTT
	
	ArrayList<String> theMotif = new ArrayList<String>();
	
	//feed in the list of chars you are using and the length a string should be with the list of char
	public void generateMotif(int k, char[] charset) {
		printAllPossibleCombo(charset, "", charset.length, k);
	}
	
	//find all possible character combinations with the char[]
	private void printAllPossibleCombo(char[] charset, String line, int n, int k) {
		if(k == 0){
			theMotif.add(line);
			return;
		}
		for(int i = 0; i < n; i++){
			String newLine = line + charset[i];
			printAllPossibleCombo(charset, newLine, n, k - 1);
		}
	}
	
	//calculate the hamming distance between two strings/substrings of the same length
	public int HD(String str0, String str1) {
		int count = 0;
		for(int i = 0; i < str0.length(); i++) {
			if(str0.charAt(i) != str1.charAt(i)) {
				count++;
			}
		}
		return count;
	}
	
	public static void main(String[] args) throws IOException{
		File input_file = new File("input.txt");
		Scanner input = new Scanner(input_file);
		Seq motif = new Seq();
		
		//get n l m p
		String[] initial_nlmp = input.nextLine().split(" ");
		int[] nlmp = {Integer.parseInt(initial_nlmp[0]),Integer.parseInt(initial_nlmp[1]),Integer.parseInt(initial_nlmp[2]),Integer.parseInt(initial_nlmp[3])};
		
		
		int n = 0, l = 0;
		final int d = 1, m = 4;
		motif.generateMotif(m, new char[] {'A','C','T','G'});
		ArrayList<String> candidates = new ArrayList<String>();
		ArrayList<String> accepted_candidates = new ArrayList<String>();
		
		String[][] substrings;
		ArrayList<String> input_strings = new ArrayList<String>();
		String user_input = "";
		
//		System.out.println("Please enter the input strings and separate them with a ',' without spaces:");
		
		for(int i = 0; i<nlmp[0]; i++) {
			user_input = input.nextLine();
			String str = "";
			for(int j = 0; j<nlmp[1];j++) {
				str+=""+user_input.charAt(j);
			}
			input_strings.add(str);
		}
		
		//split the user input into the input strings via a delimiter ','
//		input_strings = user_input.split(",");
		
//		n = input_strings.length;
//		l = input_strings[0].length();
		substrings = new String[nlmp[0]][nlmp[1]-(nlmp[2]-1)];
		
		//create substrings of input strings
		for(int i = 0; i<nlmp[0];i++) {
			for(int j = 0; j<nlmp[1]-(nlmp[2]-1);j++) {
				String substring = "" + input_strings.get(i).charAt(j)+input_strings.get(i).charAt(j+1)+input_strings.get(i).charAt(j+2)+input_strings.get(i).charAt(j+3);
				substrings[i][j] = substring;
			}
		}
		
		//check hamming distance of candidates and substrings of input strings to find potential candidates
		for(int i = 0; i<n; i++) {
			for(int j = 0; j<l-(m-1); j++) {
				for(String candidate : motif.theMotif) {
					if(motif.HD(candidate, substrings[i][j]) == 1) {
						candidates.add(candidate);
					}
				}
			}
		}
		//finding accepted candidates
		for(String candidate : candidates) {
			int count = 0;
			for(int i = 0; i<n; i++) {
				for(int j = 0; j<l-(m-1); j++) {
					if(motif.HD(candidate, substrings[i][j]) == 1) {
						count++;
					}
				}
			}
			if(count == 3 && !accepted_candidates.contains(candidate)) {
				accepted_candidates.add(candidate);
			}
		}
		
		
		//for testing, comment out when done
		System.out.println("Input Strings:");
		for(String a : input_strings) {
			System.out.println(a);
		}
		System.out.println("");
		System.out.println("Substrings per Input String:");
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < l-(m-1); j++) {
				System.out.println(substrings[i][j]);
			}
			System.out.println("");
		}
		System.out.println("The Motif:");
		for(String atcg : motif.theMotif) {
			System.out.println(atcg);
		}
		System.out.println("");
		System.out.println("Potential Candidates:");
		for(String potential_accepted : candidates) {
			System.out.println(potential_accepted);
		}
		System.out.println("");
		System.out.println("Accepted Candidates:");
		for(String accepted : accepted_candidates) {
			System.out.println(accepted);
		}
		//////////////
		
		input.close();
	}
}
