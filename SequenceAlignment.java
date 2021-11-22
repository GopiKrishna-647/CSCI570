import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.*;

public class SequenceAlignment {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("/Users/gopi/Desktop/BaseTestcases_CS570FinalProject/input1.txt");
		finalProject(file);
		//sequenceAlignment("AGGGCT", "AGGCA");
	}
	
	public static void finalProject(File input){

		// Array list for j and k values
		ArrayList<Integer> arrj = new ArrayList<Integer>();
		ArrayList<Integer> arrk = new ArrayList<Integer>();

		// First and Second base strings
		String first = null;
		String second = null;

		boolean jDone = false;

		try{
			Scanner read = new Scanner(input);
			while(read.hasNextLine()){
				String line = read.nextLine();

				if(first == null){
					first = line;
				}
				else if((line.charAt(0) == 'A') ||
						(line.charAt(0) == 'C') ||
						(line.charAt(0) == 'G') ||
						(line.charAt(0) == 'T')){
					second = line;
					jDone = true;
				}
				else{
					if(!jDone){
						arrj.add(Integer.parseInt(line));
					}
					else{
						arrk.add(Integer.parseInt(line));
					}
				}
			}

		}
		catch(FileNotFoundException e) {
			System.out.println("An error occurred while parsing the input.txt file.");
			e.printStackTrace();
		}

		// Parse first base string
		String X = parseString(first, arrj);   
		Integer predictedLengthX = (int)Math.pow(2, arrj.size()) * first.length();
		if(X.length() != predictedLengthX) {
			System.out.println("Not same length");
		}

		// Parse second base string
		String Y = parseString(second, arrk);   
		Integer predictedLengthY = (int)Math.pow(2, arrk.size()) * second.length();
		if(Y.length() != predictedLengthY) {
			System.out.println("Not same length");
		}

		sequenceAlignment(X, Y);

		System.out.println(X);
		System.out.println(Y);

	}
	
	public static String parseString(String str, ArrayList<Integer> arr){

		for (int i = 0; i < arr.size(); i++){
			String prev = str;
			str = prev.substring(0,arr.get(i)) + prev + prev.substring(arr.get(i), prev.length());
		}

		return str;
	}
	
	public static void sequenceAlignment(String X, String Y) {

		// 2D arr with length of each string
		int m = X.length()+1;
		int n = Y.length()+1;
		int[][] seqArr = new int[m][n];
		int gapPenalty = 30;

		// initialize
		for (int i = 0; i < m; i++) {
			seqArr[i][0] = i * 30;
		}

		// initialize
		for (int j = 0; j < n; j++) {
			seqArr[0][j] = j * 30;
		}

		// opt loop
		for (int j = 1; j < n; j++) {
			for (int i = 1; i < m; i++) {
				if(X.charAt(i-1) == Y.charAt(j-1)) {
					seqArr[i][j] = seqArr[i-1][j-1];
				} else {
					seqArr[i][j] = Math.min(seqArr[i - 1][j - 1] + misMatch(X.charAt(i-1), Y.charAt(j-1)),  Math.min(seqArr[i][j - 1] + gapPenalty, seqArr[i - 1][j] + gapPenalty));
				}
			}
		}


		// For Testing Purposes:
		for (int[] x1 : seqArr) {
			for (int y : x1) {
				System.out.print(y + " ");
			}
			System.out.println();
		}

		// TODO: Go through the 2D Array seqArr, print output to file output.txt


	}

	
	// mismatch penalty
	public static int misMatch(char x, char y) {
		int mismatch = 0;
		if (x == y) {
			return 0;
		} else if ((x == 'A' && y == 'C') || (x == 'C' && y == 'A')) {
			mismatch = 110;
		} else if ((x == 'A' && y == 'G') || (x == 'G' && y == 'A')) {
			mismatch = 48;
		} else if ((x == 'A' && y == 'T') || (x == 'T' && y == 'A')) {
			mismatch = 94;
		} else if ((x == 'C' && y == 'G') || (x == 'G' && y == 'C')) {
			mismatch = 118;
		} else if ((x == 'C' && y == 'T') || (x == 'T' && y == 'C')) {
			mismatch = 48;
		} else if ((x == 'G' && y == 'T') || (x == 'T' && y == 'G')) {
			mismatch = 110;
		}
		return mismatch;

	}

}
