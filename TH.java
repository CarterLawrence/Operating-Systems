import java.util.ArrayList;
public class TH implements Runnable {
			par h; // the motif (h is a bad name, but okay)
			ArrayList<String> candidates;
			ArrayList<String> accepted_candidates;
	
			String[][] substrings;
			
			int n = 0, l = 0, m = 0, d = 0, t = 0, ending = 0, beginning = 0, substring_count = 0;
			boolean done, kill_thread;
			
			
			public TH(par motif, int[] nlmpt, String[][] substrings, int substring_count, int thread_number) {
				this.h = motif;
				this.substring_count = substring_count;
				done = false;
				kill_thread = false;
				this.substrings = substrings;
				this.candidates = new ArrayList<String>();
				this.accepted_candidates = new ArrayList<String>();
				this.n = nlmpt[0];
				this.l = nlmpt[1];
				this.m = nlmpt[2];
				this.d = nlmpt[3];
				this.t = thread_number;
				ending = this.n/nlmpt[3]; // when going through the for loop we only want to do t*ending, nothing after and nothing before
				beginning = (ending*this.t)-ending;
				if(beginning == ending) done = true;
			}
			
			
		public void run(){
			if(done == false) {
				//check hamming distance of candidates and substrings of input strings to find potential candidates
				for(int i = beginning; i<ending; i++) {
					for(int j = 0; j<substring_count; j++) {
						for(String candidate : h.theMotif) {
							if(h.HD(candidate,substrings[i][j]) == d) {
								candidates.add(candidate);
							}
						}
					}
				}
				System.out.println(candidates.size());
				//finding accepted candidates
				for(String candidate : candidates) {
					int count = 0;
					for(int i = beginning; i<ending; i++) {
						for(int j = 0; j<substring_count; j++) {
							if(h.HD(candidate,substrings[i][j]) == d) {
								count++;
							}
						}
					}
					if(count == 3 && !accepted_candidates.contains(candidate)) {
						accepted_candidates.add(candidate);
					}
				}
				System.out.println(accepted_candidates.size());
				done = true;
				// keep thread alive until it's time to kill the thread
//				do{
//					try {
//						System.out.println("Waiting...");
//						java.lang.Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}while(kill_thread == false);
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
		public ArrayList<String> getAcceptedCandidates(){
			return accepted_candidates;
		}
//		private String allCandidatesSize() {
//			return getCandidates().size()+" "+getPotentialCandidates().size()+" "+getAcceptedCandidates().size();
//		}
		
	}