package org_src_vs00264_project_com1028;

import java.util.Calendar;
import java.util.Date;

/**
 * The class which holds the information for a forecast
 * @author Vytenis
 *
 */
public class Forecast {

	/**
	 * Attributes
	 */
	
	/**
	 * The product this forecast is assigned to
	 */
	private Product product;
	
	/**
	 * Whether or not the forecast is made via the statistical method
	 */
	private boolean isStatistical;
	
	/**
	 * The input used for the program
	 */
	private double[] input = new double[12];
	
	/**
	 * Whether or not the forecast is confirmed
	 */
	private boolean confirmed;
	
	/**
	 * The result received after putting the data through
	 */
	private double[] prediction = new double[3];
	
	/**
	 * The real data which will be received after the time comes
	 */
	private double[] realOutput = new double[3];
	
	/**
	 * When the forecast was generated
	 */
	private Date entryDate;
	
	/**
	 * The error of the forecast which the program predicted
	 */
	private double error;
	
	/**
	 * The actual percentage difference between the prediction and real result
	 */
	private double actualError;
	
	/**
	 * The constructor for generating a forecast
	 * @param product
	 * @param isStatistical
	 * @param input
	 * @param prediction
	 * @param error
	 */
	public Forecast(Product product, boolean isStatistical, double[] input, double[] prediction, double error) {
		super();
		this.product = product;
		this.isStatistical = isStatistical;
		this.input = input;
		confirmed = false;
		this.prediction = prediction;
		this.error = error;
		
		Calendar calendar = Calendar.getInstance();
		entryDate = calendar.getTime();
	}
	
	/**
	 * The constructor for loading a forecast when parsing the state.txt
	 * @param product
	 * @param isStatistical
	 * @param confirmed
	 * @param entryDate
	 * @param error
	 * @param actualError
	 * @param input
	 * @param prediction
	 * @param realOutput
	 */
	public Forecast(Product product, boolean isStatistical, boolean confirmed, Date entryDate, double error, double actualError, double[] input, double[] prediction, double[] realOutput) {
		super();
		this.product = product;
		this.isStatistical = isStatistical;
		this.confirmed = confirmed;
		this.entryDate = entryDate;
		this.input = input;
		this.prediction = prediction;
		this.realOutput = realOutput;
		this.error = error;
		this.actualError = actualError;
	}
	
	/**
	 * Getters for all the variables
	 * @return
	 */
	
	public Date getDate() {
		return this.entryDate;
	}
	public Product getProduct() {
		return this.product;
	}
	
	public double[] getInput() {
		return this.input;
	}
	
	public double[] getOutput() {
		return this.prediction;
	}
	
	public boolean getStatisticity() {
		return this.isStatistical;
	}
	
	public boolean getConfirmed() {
		return this.confirmed;
	}
	
	public double getConfidence() {
		return this.error;
	}
	
	/**
	 * Returns the string which represents the state of this forecast
	 * @return
	 */
	public String getSaveState() {
		String output = "";
		output += product.getName()+";";
		output += isStatistical+";";
		output += confirmed+";";
		output += entryDate.toString()+";";
		output += error+";";
		output += actualError+";";
		
		for(int i = 0; i < 12; i++) {
			output += input[i]+";";
		}
		
		for(int i = 0; i < 3; i++) {
			output += prediction[i]+";";
		}
		
		for(int i = 0; i < 3; i++) {
			output += realOutput[i]+";";
		}
		
		output += ";SUBSPLIT;";
		
		return output;
	}
}
