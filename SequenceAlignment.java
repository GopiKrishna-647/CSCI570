import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.lang.Math;
import java.util.*;

public class SequenceAlignment {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File input = new File(System.getProperty("user.dir") + "/input.txt");

		// Create output file if it does not exist
		createOutputFile();

		finalProject(input);
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
			// TODO: Throw Error?
			System.out.println("Not same length");
		}

		// Parse second base string
		String Y = parseString(second, arrk);
		Integer predictedLengthY = (int)Math.pow(2, arrk.size()) * second.length();
		if(Y.length() != predictedLengthY) {
			// TODO: Throw Error?
			System.out.println("Not same length");
		}

		// // Make sure the longer string represents Columns while shorter string represents Rows
		// //Y = columns
		// //X = rows
		// String temp = "";
		// if(X.length() > Y.length()){
		// 	temp = Y;
		// 	Y = X;
		// 	X = temp;
		// }

		sequenceAlignment(X, Y);
	}

	public static String parseString(String str, ArrayList<Integer> arr){

		for (int i = 0; i < arr.size(); i++){
			String prev = str;

			str = prev.substring(0,arr.get(i)+1) + prev + prev.substring(arr.get(i)+1, prev.length());
		}

		return str;
	}

	public static void sequenceAlignment(String X, String Y) {

		// 2D arr with length of each string
		int n = X.length()+1;	// Row
		int m = Y.length()+1; // Column

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
		for (int i = 1; i < m; i++) {
			for (int j = 1; j < n; j++) {
				if(Y.charAt(i-1) == X.charAt(j-1)) {
					seqArr[i][j] = seqArr[i-1][j-1];
				} else {
					seqArr[i][j] = Math.min(seqArr[i - 1][j - 1] + misMatch(Y.charAt(i-1),X.charAt(j-1)),
													Math.min(seqArr[i][j - 1] + gapPenalty, seqArr[i - 1][j] + gapPenalty));
				}
			}
		}

		// //For Testing Purposes:
		// for (int[] x1 : seqArr) {
		// 	for (int y : x1) {
		// 		System.out.print(y + " ");
		// 	}
		// 	System.out.println();
		// }

		String outputX = "";
		String outputY = "";

		int i = m-1; // X Columns
		int j = n-1; // Y Rows
		while((i > 0) || (j > 0)){

			// If X and Y characters at i-1 and j-1 are same, do nothing
			if((i > 0) && (j > 0) && (Y.charAt(i-1) == X.charAt(j-1))){
					outputY = outputY + String.valueOf(Y.charAt(i-1));
					outputX = outputX + String.valueOf(X.charAt(j-1));
					i--;
					j--;
			}
			// If value of seqArr[i][j] is from vertical line gap penalty
			else if((j > 0) && (seqArr[i][j] == (seqArr[i][j - 1] + gapPenalty))){
				outputY = outputY + "_";
				outputX = outputX + String.valueOf(X.charAt(j-1));
				j--;
			}

			// If value of seqArr[i][j] is from horizontal line gap penalty
			else if((i > 0) && (seqArr[i][j] == (seqArr[i-1][j] + gapPenalty))){
				outputY = outputY + String.valueOf(Y.charAt(i-1));
				outputX = outputX + "_";
				i--;
			}

			// If value of seqArr[i][j] is from a mismatch and not gap penalty
			else if((i > 0) && (j > 0) && (seqArr[i][j] == (seqArr[i - 1][j - 1] + misMatch(Y.charAt(i-1),X.charAt(j-1)))) ){
				outputY = outputY + String.valueOf(Y.charAt(i-1));
				outputX = outputX + String.valueOf(X.charAt(j-1));
				i--;
				j--;
			}
		}

		StringBuffer reverseX = new StringBuffer(outputX);
		reverseX.reverse();
		StringBuffer reverseY = new StringBuffer(outputY);
		reverseY.reverse();

		outputX = reverseX.toString();
		outputY = reverseY.toString();

		// // For Testing Purposes
		// System.out.println("X: " + X);
		// System.out.println("Y: " + Y);
		// System.out.println("outputX: " + outputX);
		// System.out.println("outputY: " + outputY);

		// Parse data for the output File
		String firstX = outputX;
		String firstY = outputY;
		String secondX = outputX;
		String secondY = outputY;

		if (outputX.length() > 50){
			firstX = outputX.toString().substring(0,50);
			secondX = outputX.toString().substring(outputX.length()-50,outputX.length()-1);

		}

		if (outputY.length() > 50){
			firstY = outputY.toString().substring(0,50);
			secondY = outputY.toString().substring(outputY.length()-50,outputY.length()-1);
		}

		// line1: First 50 X, First 50 Y
		// line2: Last 50 X, Last 50 Y
		String line1 = firstX + " " + firstY;
		String line2 = secondX + " " + secondY;

		addToOutputFile(line1, line2);
	}

	/**
	* Create the output.txt File
	* If it already exists, delete it and create a new one
	*/
	public static void createOutputFile(){
		File output = new File(System.getProperty("user.dir"), "output.txt");

		if(output.exists()){
			output.delete();
		}

		try{
			output.createNewFile();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	// Add data to the output.txt file
	public static void addToOutputFile(String line1, String line2) {

		try{
			FileWriter output = new FileWriter(System.getProperty("user.dir") + "/output.txt", true);

			BufferedWriter buffer = new BufferedWriter(output);

			// Write Line1 to output file
			if(line1!= null || !(line1 == "")){
				buffer.write(line1);
				buffer.newLine();
			}

			// Write line2 to output file
			if(line2!= null || !(line2 == "")){
				buffer.write(line2);
				buffer.newLine();
			}

			buffer.close();
			output.close();
		}
		catch(Exception e){
			System.out.println("An error occurred while writing to the output.txt file.");
			e.printStackTrace();
		}


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
