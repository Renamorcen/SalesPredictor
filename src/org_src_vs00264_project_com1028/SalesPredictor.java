package org_src_vs00264_project_com1028;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JDialog;

/**
 * The main class of the product
 * 
 * @author Vytenis
 *
 */
public class SalesPredictor {
	/**
	 * Attributes to keep track of all of the forecasts and products in the working
	 * memory. Also keeps the reference to the neural network. Keeps the reference
	 * to its parent window to affect it.
	 */
	private static ArrayList<Product> productList = new ArrayList<Product>();
	private static ArrayList<Forecast> forecastList = new ArrayList<Forecast>();
	private static NeuralNetwork neuralNetwork;
	private static MainWindow parentWindow;

	public static void main(String[] args) {
		parentWindow = new MainWindow();
		neuralNetwork = NeuralNetwork.getInstance();
	}

	/**
	 * getters
	 */

	/**
	 * Returns the product object at a specific index
	 * 
	 * @param id
	 * @return
	 */
	public static Product getProductAtId(int id) {
		return productList.get(id);
	}

	/**
	 * Returns an array of strings containing product names
	 * 
	 * @return
	 */
	public static String[] getProductNames() {
		String[] names = new String[productList.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = productList.get(i).getName();
		}
		return names;
	}

	/**
	 * Returns a two-dimensional array in a form such that it could be put in a
	 * jTable. Contains the date and the product name of the forecast
	 * 
	 * @return
	 */

	public static Object[][] getTabledForecastList() {
		Object[][] output = new Object[forecastList.size()][2];
		for (int i = 0; i < forecastList.size(); i++) {
			output[i][0] = forecastList.get(i).getDate().toString();
			output[i][1] = forecastList.get(i).getProduct().getName();
			if (forecastList.get(i).getStatisticity()) {
				output[i][1] += " - Statistical";
			} else {
				output[i][1] += " - Neural";
			}
		}
		return output;
	}

	/**
	 * returns a forecast in a specific index
	 * 
	 * @param index
	 * @return
	 */
	public static Forecast getForecastAtIndex(int index) {
		return forecastList.get(index);
	}

	/**
	 * Deletes a product in a specific index
	 * 
	 * @param index
	 */
	public static void deleteProduct(int index) {
		productList.remove(index);
		parentWindow.updateWindow();
	}

	/**
	 * Deletes a forecast in a specific index
	 * 
	 * @param index
	 */
	public static void deleteForecast(int index) {
		forecastList.remove(index);
	}

	/**
	 * Method for adding a product. Create a new product with the name that has been
	 * passed on, and then add it to the product list. Finally, update the main
	 * window so it shows up.
	 * 
	 * @param name
	 */
	public static void addProduct(String name) {
		Product newProduct = new Product(name);
		productList.add(newProduct);
		parentWindow.updateWindow();
	}

	/**
	 * The method for adding a product when reading from a file. In those cases, the
	 * product will have some entries already associated with it.
	 * 
	 * @param name
	 * @param entries
	 */
	public static void addProduct(String name, String[] entries) {
		Product newProduct = new Product(name, entries);
		productList.add(newProduct);
		parentWindow.updateWindow();
	}

	/**
	 * Gets all of the possible 15-long chunks of entries from a product, and passes
	 * them on to the neural network to train it.
	 * 
	 * @param product
	 */
	public static void trainNetwork(Product product) {
		DoneDialog dDialog = new DoneDialog();
		dDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dDialog.setVisible(true);
		Entry[] entryList = product.getEntryList();
		for (int i = 0; i < entryList.length - 15; i++) {
			neuralNetwork.train(
					new double[] { entryList[i].getSales(), entryList[i + 1].getSales(), entryList[i + 2].getSales(),
							entryList[i + 3].getSales(), entryList[i + 4].getSales(), entryList[i + 5].getSales(),
							entryList[i + 6].getSales(), entryList[i + 7].getSales(), entryList[i + 8].getSales(),
							entryList[i + 9].getSales(), entryList[i + 10].getSales(), entryList[i + 11].getSales(), },
					new double[] { entryList[i + 12].getSales(), entryList[i + 13].getSales(),
							entryList[i + 14].getSales(), });
		}
		dDialog.flip();
	}

	/**
	 * gets the latest 12 entries from a product and passes them through the neural
	 * network. Creates a forecast and adds it to the list.
	 * 
	 * @param product
	 */
	public static void produceNeuralNetworkForecast(Product product) {
		DoneDialog dDialog = new DoneDialog();
		dDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dDialog.setVisible(true);
		double[] input = product.getLastSales();
		double[] output = neuralNetwork.produceForecast(input);

		Forecast forecast = new Forecast(product, false, input, output, neuralNetwork.getLatestError());

		forecastList.add(forecast);
		dDialog.flip();
	}

	/**
	 * Produces a statistical forecast by comparing the changes in the latest 12
	 * entries with all of the possible other sets of changes Picks the one closest
	 * Extrapolates potential price changes from that one
	 * 
	 * @param product
	 */
	public static void produceStatisticalForecast(Product product) {
		DoneDialog dDialog = new DoneDialog();
		dDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dDialog.setVisible(true);
		double[] input = product.getLastSales();
		double[] deltaSample = new double[11];
		for (int i = 0; i < 11; i++) {
			deltaSample[i] = (input[i + 1] / input[i]) - 1;
		}
		ArrayList<double[]> chunks = getChangeChunks();
		double[] errors = getErrors(chunks, deltaSample);
		int lowest = getLowestError(errors);
		double[] output = calculateStatisticalPrediction(input, chunks.get(lowest));
		Forecast forecast = new Forecast(product, true, input, output, errors[lowest]);
		forecastList.add(forecast);
		dDialog.flip();
	}

	/**
	 * Calculates the future price using the pattern with the lowest error
	 * 
	 * @param input
	 * @param lowestChunk
	 * @return
	 */
	private static double[] calculateStatisticalPrediction(double[] input, double[] lowestChunk) {
		double[] output = new double[3];
		output[0] = input[11] * (lowestChunk[11] + 1);
		output[1] = input[11] * (lowestChunk[11] + 1) * (lowestChunk[12] + 1);
		output[2] = input[11] * (lowestChunk[11] + 1) * (lowestChunk[12] + 1) * (lowestChunk[13] + 1);
		for(int i = 0; i < 3; i++) {
			System.out.println(output[i]);
		}
		return output;
	}

	/**
	 * Returns the index of the lowest error in the array.
	 * 
	 * @param errors
	 * @return
	 */
	private static int getLowestError(double[] errors) {
		int output;
		if (errors[0] == 0) {
			output = 1;
		} else {
			output = 0;
		}
		for (int i = 2; i < errors.length; i++) {
			if (errors[output] > errors[i] && errors[i] != 0) {
				output = i;
			}
		}
		return output;
	}

	/**
	 * Returns an array of doubles in which each member corresponds to the error of
	 * each member of the sample with respect to the specimen
	 * 
	 * @param sample
	 * @param specimen
	 * @return
	 */
	private static double[] getErrors(ArrayList<double[]> sample, double[] specimen) {
		double[] output = new double[sample.size()];
		for (int i = 0; i < output.length; i++) {
			double errorsum = 0;
			double[] member = sample.get(i);
			for (int j = 0; j < specimen.length; j++) {
				errorsum += (specimen[j] - member[j]) * (specimen[j] - member[j]);
			}
			output[i] = errorsum;
		}
		return output;
	}

	/**
	 * Gets changes of prices in chunks of length 14
	 * 
	 * @return
	 */
	private static ArrayList<double[]> getChangeChunks() {
		ArrayList<double[]> output = new ArrayList<double[]>();
		for (int i = 0; i < productList.size(); i++) {
			if (productList.get(i).getEntryList().length >= 14) {
				double[] e = getChangesInPrices(i);
				output = chunkify(e);
			}
		}
		return output;
	}

	/**
	 * Takes a long sequence and divides it up to smaller sequences of length 14;
	 * Returns them in an arraylist of doubles
	 * 
	 * @param sequence
	 * @return
	 */
	private static ArrayList<double[]> chunkify(double[] sequence) {
		ArrayList<double[]> output = new ArrayList<double[]>();
		for (int j = 0; j < sequence.length - 13; j++) {
			double[] c = new double[14];
			for (int k = 0; k < 14; k++) {
				c[k] = sequence[k];
			}
			output.add(c);
		}
		return output;
	}

	/**
	 * Gets the whole list of changes in prices in product at index i.
	 * 
	 * @param i
	 * @return
	 */
	private static double[] getChangesInPrices(int i) {
		Product specificProduct = productList.get(i);
		double[] output = new double[specificProduct.getEntryList().length - 1];
		for (int j = 0; j < output.length; j++) {
			double secondSale = specificProduct.getEntryList()[j + 1].getSales();
			double firstSale = specificProduct.getEntryList()[j].getSales();
			output[j] = (secondSale / firstSale) - 1;
		}
		return output;
	}

	/**
	 * Generates a string representing the state of the program and writes it to a
	 * file named state.txt
	 */
	public static void save() {
		DoneDialog dDialog = new DoneDialog();
		dDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dDialog.setVisible(true);
		String saveState = "";
		saveState += neuralNetwork.getSaveState();
		for (Product p : productList) {
			saveState += p.getSaveState();
		}
		saveState += ";BIGSPLIT;";
		for (Forecast f : forecastList) {
			saveState += f.getSaveState();
		}
		writeStateToFile(saveState);
		dDialog.flip();
	}

	/**
	 * Writes the specified string to a file state.txt
	 * 
	 * @param state
	 */
	private static void writeStateToFile(String state) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("state.txt", "UTF-8");
			writer.println(state);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a link with which it is possible to download a lot of data
	 * 
	 * @param ticker
	 * @param years
	 * @return
	 */
	public static String downloadEntries(String ticker, int years) {
		Calendar calendar = Calendar.getInstance();
		long todayMillis = calendar.getTimeInMillis();
		long today = todayMillis / 1000;
		long year = 31536000 * years;
		long yearAgo = today - year;
		String link = "https://query1.finance.yahoo.com/v7/finance/download/" + ticker + "?period1=" + yearAgo
				+ "&period2=" + today + "&interval=1d&events=history&crumb=uWrB9c7Xmtk";
		return link;
	}

	/**
	 * Loads the contents of the file to the program
	 * 
	 * @param file
	 */
	public static void loadState(File file) {
		String state = readStateFile(file);
		neuralNetwork.loadState(state);
		loadProducts(state);
		loadForecasts(state);
		parentWindow.updateWindow();
	}

	/**
	 * Gets all of the data from the state regarding the forecasts and creates
	 * forecast objects.
	 * 
	 * @param state
	 */
	private static void loadForecasts(String state) {
		String[] firstBreak = state.split(";BIGSPLIT;");
		String[] secondBreak = firstBreak[2].split(";SUBSPLIT;");
		for (String s : secondBreak) {
			String[] subsplit = s.split(";");
			for (int i = 0; i < subsplit.length; i += 24) {
				double[] input = parseSpecificDoubleValues(i + 6, i + 17, subsplit);
				double[] prediction = parseSpecificDoubleValues(i + 18, i + 20, subsplit);
				double[] realOutput = parseSpecificDoubleValues(i + 21, i + 23, subsplit);
				Product product = getProductWithName(subsplit[i]);
				Date temp = parseDate(subsplit, i);
				Forecast forecast = new Forecast(product, Boolean.parseBoolean(subsplit[i + 1]),
						Boolean.parseBoolean(subsplit[i + 2]), temp, Double.parseDouble(subsplit[i + 4]),
						Double.parseDouble(subsplit[i + 5]), input, prediction, realOutput);
				forecastList.add(forecast);
			}
		}
	}

	/**
	 * extracts specific values from a string between two specific indexes. Returns
	 * them in an array of doubles From is included To is included
	 * 
	 * @param from
	 * @param to
	 * @param input
	 * @return
	 */
	private static double[] parseSpecificDoubleValues(int from, int to, String[] input) {
		double[] output = new double[to - from + 1];
		for (int i = 0; i < to - from + 1; i++) {
			output[i] = Double.parseDouble(input[i + from]);
		}
		return output;
	}

	/**
	 * Parses the date at the specified index from the provided input and returns a
	 * date object
	 * 
	 * @param input
	 * @param index
	 * @return
	 */
	private static Date parseDate(String[] input, int index) {
		DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		Date output = null;
		try {
			output = df.parse(input[index + 3]);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * Gets the reference to the product object which has the specified name
	 * 
	 * @param name
	 * @return
	 */
	private static Product getProductWithName(String name) {
		Product output = null;
		for (Product p : productList) {
			if (p.getName().equals(name)) {
				output = p;
			}
		}
		return output;
	}

	/**
	 * Extracts the product and entry data from the state string and loads them to
	 * the program
	 * 
	 * @param state
	 */
	private static void loadProducts(String state) {
		String[] firstBreak = state.split(";BIGSPLIT;");
		String[] secondBreak = firstBreak[1].split(";SUBSPLIT;");
		for (String s : secondBreak) {
			String[] subsplit = s.split(";");
			String[] entries = new String[subsplit.length - 1];
			for (int i = 1; i < subsplit.length; i++) {
				entries[i - 1] = subsplit[i];
			}
			addProduct(subsplit[0], entries);
		}
	}

	/**
	 * Reads the contents of a specific file and returns it as a string
	 * 
	 * @param file
	 * @return
	 */
	private static String readStateFile(File file) {
		String output = "";
		ArrayList<String> fileContents = new ArrayList<String>();
		BufferedReader br;
		String l;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((l = br.readLine()) != null) {
				fileContents.add(l);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		output = fileContents.get(0);
		return output;
	}
}
