import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.lang.Math;
import java.util.*;

public class Efficient {
	static Double sum = 0.0;

	public static void main(String[] args) {

		// Define variables to calculate time and memory for the basic algorithm
		long startTime = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();
		long startMemory = runtime.totalMemory() - runtime.freeMemory();

		// Input File
		File input = new File(System.getProperty("user.dir") + "/" + args[0]);
		
		//File input = new File(System.getProperty("user.dir") + "/" + "input.txt");

		if(input.exists()){
			// Create output file if it does not exist
			createOutputFile();

			// Parse the input file and Call the Basic Algorithm function
			finalProject(input);


			// Calculate Time and Memory for the Basic Algorithm
			long endMemory = runtime.totalMemory() - runtime.freeMemory();
			Double memoryTaken = (endMemory - startMemory)/1024.0;
			long endTime = System.currentTimeMillis();
			Double timeTaken = (endTime - startTime)/1000.0;
			addToOutputFile(timeTaken.toString(), "");
			addToOutputFile(memoryTaken.toString(), "");
		}
		else{
			System.out.println("input.txt file was not found.");
		}

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

		Map<String, String> result = dcDp(X, Y);
		
		parseDataForOutputFile(result.get("X"), result.get("Y"), sum);
	}
	
	public static Map<String, String> sequenceAlignment(String X, String Y) {

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

		Double minPenalty = Double.valueOf(seqArr[m-1][n-1]);
		sum+= minPenalty;
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
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("X", outputX);
		result.put("Y", outputY);
		return result;

		// // For Testing Purposes
		// System.out.println("X: " + X);
		// System.out.println("Y: " + Y);
		// System.out.println("outputX: " + outputX);
		// System.out.println("outputY: " + outputY);
	}
	
	public static Map<String, String> dcDp(String X, String Y) {
		int m = X.length();
		int n = Y.length();
		if(m <= 2 || n <= 2) {
			return sequenceAlignment(X, Y);
		}
		String firstHalfX = X.substring(0, X.length() /2);
		String secondHalf = X.substring(X.length()/2, X.length());
		
		int[][] seqArr1 = memoryEfficientSequenceAlignemnt(firstHalfX, Y);
		int[][] seqArr2 = backwardFormulation(secondHalf, Y);
		
		int sum = seqArr1[0][1] + seqArr2[n][1];
		int index = 0;
		for(int i = 1; i < n;i++) {
			int temp = seqArr1[i][1] + seqArr2[n-i][1];
			if(temp < sum) {
				sum = temp;
				index = i;
			}
		}
		
		Map<String, String> seq1 = dcDp(firstHalfX, Y.substring(0, index));
		Map<String, String> seq2 = dcDp(secondHalf, Y.substring(index, Y.length()));
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("X", seq1.get("X") + seq2.get("X"));
		result.put("Y", seq1.get("Y") + seq2.get("Y"));
		return result;
	}
	
	public static int[][] memoryEfficientSequenceAlignemnt(String X, String Y) {
		int m = Y.length() + 1;
		int n = 2;
		int gapPenalty = 30;
		
		int[][] seqArr = new int[m][2];
		
		for(int i = 0; i < m; i++) {
			seqArr[i][0] = i * 30;
		}
		
		for(int j = 1; j < X.length()+1; j++) {
			seqArr[0][1] = j * gapPenalty;
			for(int i = 1; i < m; i++) {
				seqArr[i][1] = Math.min(seqArr[i - 1][0] + misMatch(Y.charAt(i-1),X.charAt(j-1)), Math.min(seqArr[i][0] + gapPenalty, seqArr[i - 1][1] + gapPenalty));
			}
			for(int i = 0; i < m; i++) {
				seqArr[i][0] = seqArr[i][1];
			}
		}
		return seqArr;
	}
	
	public static int[][] backwardFormulation(String X, String Y) {
		StringBuilder updatedX = new StringBuilder(X).reverse();
		StringBuilder updatedY = new StringBuilder(Y).reverse();
		return memoryEfficientSequenceAlignemnt(updatedX.toString(), updatedY.toString());
	}
	
	public static void parseDataForOutputFile(String outputX, String outputY, Double minPenalty) {
		// Parse data for the output File
		String firstX = outputX;
		String firstY = outputY;
		String secondX = outputX;
		String secondY = outputY;

		if (outputX.length() > 50){
			firstX = outputX.toString().substring(0,50);
			secondX = outputX.toString().substring(outputX.length()-50,outputX.length());

		}

		if (outputY.length() > 50){
			firstY = outputY.toString().substring(0,50);
			secondY = outputY.toString().substring(outputY.length()-50,outputY.length());
		}

		// line1: First 50 X, First 50 Y
		// line2: Last 50 X, Last 50 Y
		String line1 = firstX + " " + firstY;
		String line2 = secondX + " " + secondY;

		addToOutputFile(line1, line2);
		addToOutputFile(minPenalty.toString(), "");
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
	
	public static String parseString(String str, ArrayList<Integer> arr){

		for (int i = 0; i < arr.size(); i++){
			String prev = str;

			str = prev.substring(0,arr.get(i)+1) + prev + prev.substring(arr.get(i)+1, prev.length());
		}

		return str;
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
			if(line1!= null && !(line1 == "")){
				buffer.write(line1);
				buffer.newLine();
			}

			// Write line2 to output file
			if(line2!= null && !(line2 == "")){
				buffer.write(line2);
				buffer.newLine();
			}
      // TODO: Adding a new line at the end of file, not needed when all output has been printed.


			buffer.close();
			output.close();
		}
		catch(Exception e){
			System.out.println("An error occurred while writing to the output.txt file.");
			e.printStackTrace();
		}
	}


}
