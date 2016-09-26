package com.orchestrated.postfix;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.orchestrated.postfix.util.AppConstants;
import com.orchestrated.postfix.util.ApplicationProperties;

public class RPStackCalculator {

	public static List<CSVRecord> list = new ArrayList<CSVRecord>();
	private static final Map<String, String> columns;
	private static Stack<Double> stack = new Stack<Double>();

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
			loadData();
			readData();
	}

	private static String getCellValue(String t) {

		ArrayList<Character> chars = new ArrayList<Character>();
		for (char c : t.toCharArray()) {
			chars.add(c);
		}

		CSVRecord record = list.get(Character.getNumericValue(chars.get(1).charValue()) - 1);
		return record.get(Integer.parseInt(columns.get(Character.toString(chars.get(0).charValue()))));

	}

	public static void loadData() {

		Properties properties = ApplicationProperties.getInstance().load();
		File csvJobFile = new File(properties.getProperty(AppConstants.SALARY_DATA_FILE_PATH));
		CSVParser parser;

		try {
			FileReader csvFileReader = new FileReader(csvJobFile);
			parser = new CSVParser(csvFileReader, CSVFormat.EXCEL);
			list = parser.getRecords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static double readData(){
		double returnValue=0.0;
		for (CSVRecord record : list) {
			Iterator<String> recIterator = record.iterator();
			while (recIterator.hasNext()) {
				System.out.println();
				String token = recIterator.next();
				if (token != null && !token.isEmpty()) {
					try {
						eval(token);
						returnValue = stack.pop();// evalRPN(token);
						System.out.println("RETURN CVALUE>>" + returnValue);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} else {
					System.out.println("TODO ERROR");
				}
			}
		}
		return returnValue;
	}

	public static void eval(String token) {

		double number1;
		double number2;
		String candidate;

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
	}

}