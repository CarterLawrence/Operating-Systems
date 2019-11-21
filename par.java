import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


/* TODO:
 * check if candidate is in input string
 * divide task amongst threads more evenly
 */
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
		for(String c : candidate) {
			if(!candidates.contains(c)) candidates.add(c);
		}
	}
	//we aggregate all accepted candidates here from all lists
	public void addAcceptedCandidates(ArrayList<String> candidate) {
		for(String c : candidate) {
			if(!accepted_candidates.contains(c)) accepted_candidates.add(c);
		}
	}
	
	// prints out the final results
	public void printCandidates() throws FileNotFoundException {
		PrintStream fileOut = new PrintStream("output.txt");
		System.setOut(fileOut);
		System.out.println("Accepted Candidates:");
		for(String accepted : accepted_candidates) {
			System.out.println(accepted);
		}
	}


	
	public static void main(String[] args) throws IOException, InterruptedException{
		File input_file = new File("input.txt");
		Scanner input = new Scanner(input_file);

		//get n l m p t
		String[] initial_nlmpt = input.nextLine().split(" ");
		int[] nlmpt = {Integer.parseInt(initial_nlmpt[0]),Integer.parseInt(initial_nlmpt[1]),Integer.parseInt(initial_nlmpt[2]),Integer.parseInt(initial_nlmpt[3]),Integer.parseInt(initial_nlmpt[4])};
		
		int t = Integer.parseInt(initial_nlmpt[4]);
		
		int m = Integer.parseInt(initial_nlmpt[2]);
		par motif = new par();
		motif.generateMotif(m, new char[] {'A','C','T','G'});

		
		ArrayList<String> input_strings = new ArrayList<String>();
		ArrayList<String> sub_strings = new ArrayList<String>();
		String user_input = "";

		for(int i = 0; i<nlmpt[0]; i++) {
			user_input = input.nextLine();
			String str = "";
			for(int j = 0; j<nlmpt[1]-nlmpt[2];j++) {
				str+=""+user_input.substring(j, j+nlmpt[2])+" ";
			}
			input_strings.add(str);
		}
		
		//create substrings of input strings
		for(int i = 0; i<nlmpt[0];i++) {
			String[] substring = input_strings.get(i).split(" ");
			for(String sub : substring) {
				sub_strings.add(sub);
			}
		}
		
		Thread[] objects = new Thread[t];
		TH[] th = new TH[t];
		
		//start up threads
		for(int i = 0; i < t; i++){
			th[i] = new TH(motif, nlmpt, sub_strings, input_strings, i+1);
			objects[i] = new Thread(th[i]);
			objects[i].start();
		}
		
		for(int i = 0; i < t; i++){
			objects[i].join();
			motif.addAcceptedCandidates(th[i].getAcceptedCandidates());
		}
		motif.printCandidates();
		input.close();
	}
}

class TH implements Runnable {
			par h; // the motif (h is a bad name, but okay)
			ArrayList<String> accepted_candidates;
	
			ArrayList<String> sub_strings;
			ArrayList<String> input_strings;
			String[][] substrings;
			
			int d = 0, ending = 0, beginning = 0;
			
			
			public TH(par motif, int[] nlmpt, ArrayList<String> sub_strings, ArrayList<String> input_strings, int thread_number) {
				this.h = motif;
				this.input_strings = input_strings;
				this.sub_strings = sub_strings;
				accepted_candidates = new ArrayList<String>();
				d = nlmpt[3];
				ending = (h.theMotif.size()/nlmpt[4])*thread_number; // take the substrings size and divide them by the total num of threads, then multiply it by the current thread
				beginning = ending-(h.theMotif.size()/nlmpt[4]);
			}
			
			
		public void run(){
			//check hamming distance of candidates and substrings of input strings to find potential candidates
			for(int i = beginning; i<ending; i++) {
				String candidate = h.theMotif.get(i);
				ArrayList<String> checked_string = new ArrayList<String>();
				int count = 0;
				for(String sub : sub_strings) {
					if(h.HD(candidate,sub) == d) {
						for(String input : input_strings) {
							if(input.contains(sub) && !checked_string.contains(input)) {
								checked_string.add(input);
								count++;
								break;
							}
						}
					}
				}
				if(count == 3) {
					accepted_candidates.add(candidate);
				}
			}
		}
		public ArrayList<String> getAcceptedCandidates(){
			return accepted_candidates;
		}
		
}