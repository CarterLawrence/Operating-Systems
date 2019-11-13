import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;



public class par {

	ArrayList<String> theMotif = new ArrayList<String>();
	ArrayList<String> accepted_candidates = new ArrayList<String>();
	ArrayList<String> candidates = new ArrayList<String>();

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
	//we aggregate all candidates here from all lists
	public void addCandidates(ArrayList<String> candidate) {
		candidates.addAll(candidate);
	}
	//we aggregate all accepted candidates here from all lists
	public void addAcceptedCandidates(ArrayList<String> candidate) {
		accepted_candidates.addAll(candidate);
	}
	
	// prints out the final results
	public void printCandidates() {
		System.out.println("The Motif:");
		for(String atcg : theMotif) {
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


	
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException{
		URL path = ClassLoader.getSystemResource("input.txt");
		File input_file = new File(path.toURI());
		Scanner input = new Scanner(input_file);

		//get n l m p t
		String[] initial_nlmpt = input.nextLine().split(" ");
		int[] nlmpt = {Integer.parseInt(initial_nlmpt[0]),Integer.parseInt(initial_nlmpt[1]),Integer.parseInt(initial_nlmpt[2]),Integer.parseInt(initial_nlmpt[3])};
		
		int t = Integer.parseInt(initial_nlmpt[4]);
		
		int m = Integer.parseInt(initial_nlmpt[2]);
		par motif = new par();
		motif.generateMotif(m, new char[] {'A','C','T','G'});

		String[][] substrings;
		ArrayList<String> input_strings = new ArrayList<String>();
		String user_input = "";

		for(int i = 0; i<nlmpt[0]; i++) {
			user_input = input.nextLine();
			String str = "";
			for(int j = 0; j<nlmpt[1]-nlmpt[2];j++) {
				str+=""+user_input.substring(j, j+nlmpt[2])+" ";
			}
			input_strings.add(str);
		}
		int substring_count = input_strings.get(0).split(" ").length;
		substrings = new String[nlmpt[0]][substring_count];
		
		//create substrings of input strings
		for(int i = 0; i<nlmpt[0];i++) {
			String[] substring = input_strings.get(i).split(" ");
			int j = 0;
			for(String sub : substring) {
				substrings[i][j] = sub;
				j++;
			}
		}
		
		Thread[] objects = new Thread[t];
		TH[] th = new TH[t];
		//start up threads
		for(int i = 0; i < t; i++){
			th[i] = new TH(motif, nlmpt, substrings, substring_count, i+1);
			objects[i] = new Thread(th[i]);
			objects[i].start();
		}
		boolean done = false;
		//check if all threads are done
		int count = 0;
		while(done == false) {
			for(int i = 0; i < t; i++){
				if(th[i].isDone() == true)
					count++;
			}
			if(count == t) done = true;
		}
		for(int i = 0; i < t; i++){
			objects[i].join();
			motif.addCandidates(th[i].getCandidates());
			motif.addAcceptedCandidates(th[i].getAcceptedCandidates());
		}
		motif.printCandidates();
		input.close();
	}
}