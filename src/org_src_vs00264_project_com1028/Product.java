package org_src_vs00264_project_com1028;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * The class which holds information for a product
 * @author Vytenis
 *
 */
public class Product {

	/**
	 * The name of the product
	 */
	private String name;
	
	/**
	 * The arrayList holding all of the entries belonging to this product
	 */
	private ArrayList<Entry> entryList = new ArrayList<Entry>();
	
	/**
	 * Constructor for creating a product on the go
	 * @param name
	 */
	public Product(String name) {
		this.name = name;
	}
	
	/**
	 * Constructor for creating a product when loading from a state
	 * @param name
	 * @param entryArray
	 */
	public Product(String name, String[] entryArray) {
		this.name = name;
		for(int i = 0; i < entryArray.length; i=i+2) {
			DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
			Date temp;
			try {
				temp = df.parse(entryArray[i+1]);
				addEntry(Double.parseDouble(entryArray[i]), temp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Getters
	 */
	
	/**
	 * Returns the last 12 sales to be used by forecasts
	 * @return
	 */
	public double[] getLastSales() {
		Entry[] entryList = getEntryList();
		double[] output = new double[] {
				entryList[entryList.length-12].getSales(),
				entryList[entryList.length-11].getSales(),
				entryList[entryList.length-10].getSales(),
				entryList[entryList.length-9].getSales(),
				entryList[entryList.length-8].getSales(),
				entryList[entryList.length-7].getSales(),
				entryList[entryList.length-6].getSales(),
				entryList[entryList.length-5].getSales(),
				entryList[entryList.length-4].getSales(),
				entryList[entryList.length-3].getSales(),
				entryList[entryList.length-2].getSales(),
				entryList[entryList.length-1].getSales()};
		return output;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Object[][] getTabledEntryList(){
		Object[][] output = new Object[entryList.size()][2];
		for(int i = 0; i < entryList.size(); i++) {
			output[i][0] = entryList.get(i).getDate().toString();
			output[i][1] = entryList.get(i).getSales();
		}
		return output;
	}
	
	public Entry[] getEntryList() {
		return entryList.toArray(new Entry[entryList.size()]);
	}
	
	/**
	 * Mutators
	 */
	
	public void deleteEntry(int index) {
		entryList.remove(index);
	}
	
	/**
	 * Creates entry on the go
	 * @param value
	 */
	public void addEntry(double value) {
		Entry temp = new Entry(value);
		entryList.add(temp);
	}
	
	/**
	 * Creates entry when loading from a file
	 * @param value
	 * @param date
	 */
	public void addEntry(double value, Date date) {
		Entry temp = new Entry(date, value);
		entryList.add(temp);
	}
	
	
	/**
	 * Loads data from a file
	 * @param file
	 */
	public void addFile(File file) {
		ArrayList<String> fileContents = new ArrayList<String>();
		fileContents = readData(file);
		if(correctFormat(fileContents.get(0))) {
			addEntriesFromFile(fileContents);
		}
	}
	
	/**
	 * Reads the information from a file
	 * @param file
	 * @return
	 */
	private ArrayList<String> readData(File file) {
		BufferedReader br;
		String l;
		ArrayList<String> fileContents = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(file));
			while((l = br.readLine())!=null) {
				fileContents.add(l);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContents;
	}
	
	/**
	 * Loads the information from a file
	 * @param fileContents
	 */
	private void addEntriesFromFile(ArrayList<String> fileContents) {
		for(int i = 1; i < fileContents.size(); i++) {
			boolean dateTaken = false;
			Entry toAdd = parseEntry(fileContents.get(i));
			for(int p = 0; p < entryList.size(); p++) {
				if(entryList.get(p).getDate().equals(toAdd.getDate())) {
					entryList.set(p, toAdd);
					dateTaken = true;
				}
			}
			if(!dateTaken) {
				entryList.add(toAdd);
			}
		}
	}
	
	/**
	 * Checks whether or not the format of the string is correct and worthy to read
	 * @param fl
	 * @return
	 */
	private boolean correctFormat(String fl) {
		boolean output = false;
		if(fl.equals("Date,Open,High,Low,Close,Adj Close,Volume")) {
			output = true;
		}
		return output;
	}
	
	/**
	 * Parses an entry from the yahoo format
	 * @param in
	 * @return
	 */
	private Entry parseEntry(String in) {
		String[] components = in.split(",");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Entry output;
		try {
			output = new Entry(df.parse(components[0]), Double.parseDouble(components[1]));
		} catch (NumberFormatException e) {
			output = new Entry(0.0);
			e.printStackTrace();
		} catch (ParseException e) {
			output = new Entry(0.0);
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * Returns a string which represents the state of the product
	 * @return
	 */
	public String getSaveState() {
		String output = "";
		
		output += name+";";
		for(Entry e: entryList) {
			output += e.getSaveState();
		}
		
		output += ";SUBSPLIT;";
		
		return output;
	}
}
