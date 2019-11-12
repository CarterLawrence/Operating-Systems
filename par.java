import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;



public class par {

	ArrayList<String> theMotif = new ArrayList<String>();
	ArrayList<String> potential_candidates = new ArrayList<String>();
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
	//use number of threads for divider
	public void setDivider(int divider) {
		int candidate_amount = candidates.size()/divider;
		int potential_candidates_amount = potential_candidates.size()/divider;
		int accepted_candidates_amount = accepted_candidates.size()/divider;
		System.out.println(candidate_amount+" "+potential_candidates_amount+" "+accepted_candidates_amount+"\n"+candidates.size()+" "+potential_candidates.size()+" "+accepted_candidates.size());
		ArrayList<String> q = new ArrayList<String>(),w = new ArrayList<String>(),e = new ArrayList<String>();
		for(int i = 0; i<candidate_amount;i++) {
			q.add(candidates.get(i));
		}
		for(int i = 0; i<potential_candidates_amount;i++) {
			w.add(potential_candidates.get(i));
		}
		for(int i = 0; i<accepted_candidates_amount;i++) {
			e.add(accepted_candidates.get(i));
		}
		candidates = q;
		potential_candidates = w;
		accepted_candidates = e;
	}
	//we aggregate all candidates here from all lists
	public void addCandidates(ArrayList<String> candidate) {
		candidates.addAll(candidate);
	}
	//we aggregate all potential candidates here from all lists
	public void addPotentialCandidates(ArrayList<String> candidate) {
		potential_candidates.addAll(candidate);
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

	private static class TH extends par implements Runnable {
			par h; // the motif (h is a bad name, but okay)
			ArrayList<String> candidates;
			ArrayList<String> accepted_candidates;
	
			String[][] substrings;
			
			int n = 0, l = 0, m = 0, t = 0, ending = 0, beginning = 0;
			boolean done, kill_thread;
			
			
			public TH(par motif, int[] nlmpt, String[][] substrings, ArrayList<String> candidates, ArrayList<String> accepted_candidates, int thread_number) {
				this.h = motif;
				done = false;
				kill_thread = false;
				this.substrings = substrings;
				this.candidates = candidates;
				this.accepted_candidates = accepted_candidates;
				this.n = nlmpt[0];
				this.l = nlmpt[1];
				this.m = nlmpt[2];
				this.t = thread_number;
				ending = this.n/nlmpt[3]; // when going through the for loop we only want to do t*ending, nothing after and nothing before
				beginning = (ending*this.t)-ending;
			}
			
			
		public void run(){
			//check hamming distance of candidates and substrings of input strings to find potential candidates
			for(int i = beginning; i<ending; i++) {
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
			done = true;
			// keep thread alive until it's time to kill the thread
			while(kill_thread == false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public boolean isDone() {
			return done;
		}
		public void killThread(boolean do_it) {
			kill_thread = do_it;
		}
		public ArrayList<String> getCandidates(){
			return candidates;
		}
		public ArrayList<String> getPotentialCandidates(){
			return candidates;
		}
		public ArrayList<String> getAcceptedCandidates(){
			return candidates;
		}
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		URL path = ClassLoader.getSystemResource("input.txt");
		File input_file = new File(path.toURI());
		Scanner input = new Scanner(input_file);

		//get n l m p t
		String[] initial_nlmpt = input.nextLine().split(" ");
		int[] nlmpt = {Integer.parseInt(initial_nlmpt[0]),Integer.parseInt(initial_nlmpt[1]),Integer.parseInt(initial_nlmpt[2]),Integer.parseInt(initial_nlmpt[3])};
		
		int t = Integer.parseInt(initial_nlmpt[4]);
		
		int d = Integer.parseInt(initial_nlmpt[3]), m = Integer.parseInt(initial_nlmpt[2]);
		par motif = new par();
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
		
		Thread[] objects = new Thread[t];
		TH[] th = new TH[t];
		//start up threads
		for(int i = 0; i < t; i++){
			th[i] = new TH(motif, nlmpt, substrings, candidates, accepted_candidates, i+1);
			objects[i] = new Thread(th[i]);
			objects[i].start();
		}
		boolean done = false;
		//check if all threads are done
		while(done == false) {
			for(int i = 0; i < t; i++){
				if(th[i].isDone() == true)
					done = true;
				else done = false;
			}
		}
		for(int i = 0; i < t; i++){
			motif.addCandidates(th[i].getCandidates());
			motif.addPotentialCandidates(th[i].getPotentialCandidates());
			motif.addAcceptedCandidates(th[i].getAcceptedCandidates());
			th[i].killThread(true);
		}
		motif.setDivider(t);
		motif.printCandidates();
		
		input.close();
	}
}