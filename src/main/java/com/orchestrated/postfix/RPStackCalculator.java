package com.orchestrated.postfix;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.orchestrated.postfix.util.AppConstants;

public class RPStackCalculator {
	
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static List<CSVRecord> inputCsv = new ArrayList<CSVRecord>();
	private static List<List<String>> outputCsv = new ArrayList<List<String>>();
	private static final Map<String, String> columns;
	private static Stack<Double> stack = new Stack<Double>();
	private static String input="";
	private static String output="";
	
	// Map the csv column name vs column number

	static {
		columns = new HashMap<String, String>();
		columns.put("a", "0");
		columns.put("b", "1");
		columns.put("c", "2");
		columns.put("d", "3");
		columns.put("e", "4");
		columns.put("f", "5");
		columns.put("g", "6");
		columns.put("h", "7");
		columns.put("i", "8");
		columns.put("j", "9");
	}

	public static void main(String[] args) {
		
		if (args.length != 2) {
			throw new RuntimeException("You shouls provide two inputs to start the programe");
		}
		else{
			input=args[0];
			output=args[1];
			loadData();
			readData();
		}
	}

	private static String getCellValue(String t) throws RuntimeException{

		try {
			ArrayList<Character> chars = new ArrayList<Character>();
			for (char c : t.toCharArray()) {
				chars.add(c);
			}

			CSVRecord record = inputCsv.get(Character.getNumericValue(chars.get(1).charValue()) - 1);
			return record.get(Integer.parseInt(columns.get(Character.toString(chars.get(0).charValue()))));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Invalid data found :"+e.getMessage());
		}

	}
	
	/**
	 * Load the csv file data into the List(inputCsv). 
	 */

	private static void loadData() throws RuntimeException{

		File file = new File(RPStackCalculator.class.getProtectionDomain().getCodeSource().getLocation()
                .getFile());        
        String path = file.getParent() + File.separator + input;
		File csvJobFile = new File(path);	
		CSVParser parser=null;
		FileReader csvFileReader=null;

		try {
		    csvFileReader = new FileReader(csvJobFile);
			parser = new CSVParser(csvFileReader, CSVFormat.EXCEL);
			inputCsv = parser.getRecords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Input file not found to run the application"+path );
		}
		finally {
			try {
				csvFileReader.close();
				parser.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	/**
	 * Read data and evaluate. 
	 */
	
	private static void readData(){
		double returnValue=0.0;
		for (CSVRecord record : inputCsv) {
			Iterator<String> recIterator = record.iterator();
			List<String> row=new ArrayList<String>();
			while (recIterator.hasNext()) {
				String token = recIterator.next();
				if (token != null && !token.isEmpty()) {
					try {
						eval(token);
						returnValue = stack.pop();// evalRPN(token);
						System.out.println("RETURN CVALUE>>" + returnValue);
						row.add(returnValue+"");
					} catch (Exception e) {
						e.printStackTrace();
						row.add("#ERR");
					}
				} else {
					row.add("#ERR");
				}
			}
			outputCsv.add(row);
		}
		//write data
		write();
	}
	/**
	 * Write the output into the csv file. 
	 */
	private static void write() throws RuntimeException{

		File csvJobFile = null;
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		try {
			File file = new File(RPStackCalculator.class.getProtectionDomain().getCodeSource().getLocation()
	                .getFile());        
	        String path = file.getParent() + File.separator + output;
		    csvJobFile = new File(path);	
			fileWriter = new FileWriter(csvJobFile);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			for (List<String> row : outputCsv) {
				csvFilePrinter.printRecord(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());

		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	/**
	 * Read the specific token from the cell and evaluate. 
	 */

	private static void eval(String token) throws RuntimeException{

		double number1;
		double number2;
		String candidate;

		try {
			StringTokenizer tokenSet = new StringTokenizer(token);

			while (tokenSet.hasMoreElements()) {
				candidate = (String) tokenSet.nextElement();

				if (candidate.equals("+") || candidate.equals("-") || candidate.equals("*") || candidate.equals("/")) {

					switch (candidate.charAt(0)) {
					case '+':
						number1 = stack.pop();
						number2 = stack.pop();
						stack.push(number1 + number2);
						break;
					case '-':
						number1 = stack.pop();
						number2 = stack.pop();
						stack.push(number2 - number1);
						break;
					case '/':
						number1 = stack.pop();
						number2 = stack.pop();
						stack.push(number2 / number1);
						break;
					case '*':
						number1 = stack.pop();
						number2 = stack.pop();
						stack.push(number1 * number2);
						break;
					}
				} else if (candidate.matches(AppConstants.CELL_VALIDATION_RULE)) {
					// read token from the cell
					// pass it recursively
					eval(getCellValue(candidate));

				} else {
					stack.push(Double.parseDouble(candidate));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Invalid data found :"+e.getMessage());
		}
	}

}