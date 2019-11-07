import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



public class par {

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

	private static class TH extends par implements Runnable {
		par h = new par();
		
		ArrayList<String> candidates = new ArrayList<String>();
		ArrayList<String> accepted_candidates = new ArrayList<String>();

		String[][] substrings = new String[nlmpt[0]][nlmpt[1]-(nlmpt[2]-1)];
		
		int n = 0, l = 0;
		final int d = 1, m = 4;
		
		public void run(){
		//check hamming distance of candidates and substrings of input strings to find potential candidates
		for(int i = 0; i<n; i++) {
			for(int j = 0; j<l-(m-1); j++) {
				for(String candidate : h.theMotif) {
					if(h.HD(candidate, substrings[i][j]) == 1) {
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
					if(h.HD(candidate, substrings[i][j]) == 1) {
						count++;
					}
				}
			}
			if(count == 3 && !accepted_candidates.contains(candidate)) {
				accepted_candidates.add(candidate);
			}
		}
		System.out.println("The Motif:");
		for(String atcg : h.theMotif) {
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
		}
	public static void main(String[] args) throws IOException{
		File input_file = new File("input.txt");
		Scanner input = new Scanner(input_file);
		par motif = new par();

		//get n l m p t
		String[] initial_nlmpt = input.nextLine().split(" ");
		int[] nlmpt = {Integer.parseInt(initial_nlmpt[0]),Integer.parseInt(initial_nlmpt[1]),Integer.parseInt(initial_nlmpt[2]),Integer.parseInt(initial_nlmpt[3])};
		
		int t = Integer.parseInt(initial_nlmpt[4]);
		
		int n = 0, l = 0;
		final int d = 1, m = 4;
		motif.generateMotif(m, new char[] {'A','C','T','G'});
		ArrayList<String> candidates = new ArrayList<String>();
		ArrayList<String> accepted_candidates = new ArrayList<String>();

		String[][] substrings;
		ArrayList<String> input_strings = new ArrayList<String>();
		String user_input = "";

		for(int i = 0; i<nlmpt[0]; i++) {
			user_input = input.nextLine();
			String str = "";
			for(int j = 0; j<nlmpt[1];j++) {
				str+=""+user_input.charAt(j);
			}
			input_strings.add(str);
		}
		substrings = new String[nlmpt[0]][nlmpt[1]-(nlmpt[2]-1)];
		
		//create substrings of input strings
		for(int i = 0; i<nlmpt[0];i++) {
			for(int j = 0; j<nlmpt[1]-(nlmpt[2]-1);j++) {
				String substring = "" + input_strings.get(i).charAt(j)+input_strings.get(i).charAt(j+1)+input_strings.get(i).charAt(j+2)+input_strings.get(i).charAt(j+3);
				substrings[i][j] = substring;
			}
		}
		for(int i = 0; i < t; i++){
			Thread object = new Thread(new TH());
			object.start();
		}
		/*
		System.out.println("");
		System.out.println("Accepted Candidates:");
		for(String accepted : accepted_candidates) {
			System.out.println(accepted);
		}*/
		input.close();
	}
}
	}
