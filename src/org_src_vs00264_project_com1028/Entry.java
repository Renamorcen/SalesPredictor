package org_src_vs00264_project_com1028;

import java.util.Calendar;
import java.util.Date;

/**
 * The class which holds the information for a data entry
 * @author Vytenis
 *
 */
public class Entry {
	/**
	 * The value of the thing
	 */
	private double sales;
	
	/**
	 * The date when that value was true
	 */
	private Date entryDate;
	
	/**
	 * Constructor for generating an entry
	 * @param sales
	 */
	public Entry(double sales) {
		this.sales = sales;
		Calendar temp = Calendar.getInstance();
		entryDate = temp.getTime();
	}
	
	/**
	 * Constructor for reading an entry from a file
	 * @param entryDate
	 * @param sales
	 */
	public Entry(Date entryDate, double sales) {
		this.sales = sales;
		this.entryDate = entryDate;
	}
	
	/**
	 * getters
	 */
	
	public double getSales() {
		return this.sales;
	}
	
	public Date getDate() {
		return this.entryDate;
	}
	
	/**
	 * Returns a string which represents the state of the entry
	 * @return
	 */
	public String getSaveState() {
		String output = "";
		
		output += sales+";";
		output += entryDate.toString()+";";
		
		return output;
	}
}
