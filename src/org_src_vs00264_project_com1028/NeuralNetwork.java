package org_src_vs00264_project_com1028;

import java.util.Random;

/**
 * The neural network of the program
 * 
 * @author Vytenis
 *
 */
public class NeuralNetwork {

	private static NeuralNetwork instance = null;

	/**
	 * The error. Initialised as -1 in the case that somebody would want to create a
	 * forecast without training the network.
	 */
	double latestError = -1;

	/**
	 * Node objects of the network. THe first three layers have 12 nodes each The
	 * final layer has 3 nodes
	 */
	private static Node[][] inputNodes = new Node[28][6];
	
	private static Node[][] innerNodes = new Node[84][2];
	
	private static Node[][] outputLayer = new Node[7][6];

	/**
	 * These are temporary values for the changes in biases
	 */
	private final double[][] deltaBiasFirst = new double[28][6];
	private final double[][] deltaBiasInner = new double[84][2];
	private final double[][] deltaBiasLast = new double[7][6];

	/**
	 * These are the values for the weights between the nodes in adjacent layers
	 */
	private static double[][][] firstWeights = new double[28][6][84];
	private static double[][] secondWeights = new double[84][84];
	private static double[][][] thirdWeights = new double[84][7][6];

	/**
	 * Values for the changes in weights
	 */
	private static double[][][] firstWeightsDelta = new double[28][6][84];
	private static double[][] secondWeightsDelta = new double[84][84];
	private static double[][][] thirdWeightsDelta = new double[84][7][6];

	private static int weightResolution = 200;

	/**
	 * learning step for the weights. Chosen by trial and error such that the
	 * weights do not diverge to infinity.
	 */
	private static double stepsize = 0.000000001;

	/**
	 * learning step for the biases. Chosen by trial and error such that the biases
	 * do not diverge to infinity.
	 */
	private static double stepsizeB = 0.0001;

	/**
	 * The leak for the activation function
	 */
	private static double leak = 0.5;

	/**
	 * The dropout chance for a node
	 */
	private static double p = 0.5;

	/**
	 * Generates everything within the neural network with random variables
	 */
	protected NeuralNetwork() {
		final Random rand = new Random();
		generateInnerWeights(rand);
		generateLastWeights(rand);
		generateNodes();
		uploadPrevNodes();
	}

	public static NeuralNetwork getInstance() {
		NeuralNetwork output = null;
		if (instance == null) {
			output = new NeuralNetwork();
		}
		return output;
	}

	/**
	 * A method to generate the inner weights of the network
	 * 
	 * @param rand
	 */
	private static void generateInnerWeights(Random rand) {
		for (int i = 0; i < 2; i++) {
			for (int p = 0; p < 12; p++) {
				for (int j = 0; j < 12; j++) {
					//innerWeights[j][p][i] = (rand.nextInt(weightResolution) / (3.0 * weightResolution) - (1.0 / 12.0));
					firstWeights[j][p][i] = (rand.nextInt(weightResolution) / (1.0 * weightResolution) - (0.5));
				}
				// System.out.println("Sum of weights gointo into node "+p+" in layer "+ i + ":
				// "+sum);
			}
		}

	}

	/**
	 * Generates last weights of the network
	 * 
	 * @param rand
	 */
	private static void generateLastWeights(Random rand) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 12; j++) {
				//lastWeights[j][i] = (rand.nextInt(weightResolution) / (3.0 * weightResolution) - (1.0 / 12.0));
				thirdWeights[j][i] = (rand.nextInt(weightResolution) / (1.0 * weightResolution) - (0.5));
			}
			// System.out.println("Sum of weights gointo into node "+i+": "+sum);
		}
	}

	/**
	 * Generates nodes of the network
	 */
	private static void generateNodes() {
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 3; j++) {
				nodes[i][j] = new Node(leak);
			}
		}
		for (int i = 0; i < 3; i++) {
			outputLayer[i] = new Node(leak);
		}
	}

	/**
	 * Returns the last error of the program
	 * 
	 * @return
	 */
	public double getLatestError() {
		return latestError;
	}

	/**
	 * RUns the input through the network and returns an array of doubles as an
	 * output for the next 3 values
	 * 
	 * @param input
	 * @return
	 */
	public double[] produceForecast(double[] input) {
		final double[] output = new double[3];
		loadData(input);
		uploadWeights();
		calcOutput();
		for (int i = 0; i < 3; i++) {
			output[i] = outputLayer[i].getValue();
		}

		return output;
	}

	/**
	 * Trains the neural network given some data set and some desired output by
	 * using backpropagation
	 * 
	 * @param inputData
	 * @param desiredData
	 */
	public void train(double[] inputData, double[] desiredData) {
		dropout();
		double[] adjustedDesiredData = new double[3];
		for (int i = 0; i < 3; i++) {
			adjustedDesiredData[i] = desiredData[i] * (((1 - p) * (9.0 - 4.0 * p)) / 9.0);
		}
		produceForecast(inputData);
		calculateChangesToWeights(adjustedDesiredData);
		calculateChangesToBiases(adjustedDesiredData);
		applyChangesToWeights();
		applyChangesToBiases();
	}

	/**
	 * This method will tag a percentage of nodes as dropped out during training to
	 * prevent overfitting
	 */
	private void dropout() {
		final Random rand = new Random();
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 12; j++) {
				final double randomN = rand.nextInt(10) / 10.0;
				if (randomN < p) {
					nodes[j][i].dropout();
				}
			}
		}
	}

	/**
	 * Loads the data to the first nodes
	 * 
	 * @param input
	 */
	private void loadData(double[] input) {
		for (int i = 0; i < 12; i++) {
			nodes[i][0].loadData(input[i]);
		}
	}

	/**
	 * Uploads the weights in the network object to each of the nodes
	 */
	private void uploadWeights() {
		uploadInnerWeights();
		uploadLastWeights();
	}

	/**
	 * Uploads the weights in the network object to each node
	 */
	private void uploadInnerWeights() {
		System.out.println("uploading weights");
		for (int j = 1; j < 3; j++) {
			for (int i = 0; i < 12; i++) {
				final double[] weights = new double[12];
				for (int k = 0; k < 12; k++) {
					weights[k] = firstWeights[k][i][j - 1];
					System.out.println(weights[k]);
				}
				nodes[i][j].loadWeights(weights);
			}
		}
	}

	/**
	 * Uploads the final weights to the nodes
	 */
	private void uploadLastWeights() {
		for (int i = 0; i < 3; i++) {
			final double[] weights = new double[12];
			for (int k = 0; k < 12; k++) {
				weights[k] = thirdWeights[k][i];
				System.out.println(thirdWeights[k][i]);

			}
			outputLayer[i].loadWeights(weights);
		}
	}

	/**
	 * Uploads the pointer to its previous nodes to each node
	 */
	private static void uploadPrevNodes() {
		uploadPrevNodesInner();
		uploadPrevNodesLast();
	}

	/**
	 * Uploads the pointer to its previous nodes to each node in the inner layers
	 */
	private static void uploadPrevNodesInner() {
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 12; j++) {
				final Node[] prevNodes = new Node[12];
				for (int k = 0; k < 12; k++) {
					prevNodes[k] = nodes[k][i - 1];
				}
				nodes[j][i].uploadPrevNodes(prevNodes);
			}
		}
	}

	/**
	 * Uploads the pointer to its previous nodes to each node in the last layer
	 */
	private static void uploadPrevNodesLast() {
		for (int i = 0; i < 3; i++) {
			final Node[] prevNodes = new Node[12];
			for (int k = 0; k < 12; k++) {
				prevNodes[k] = nodes[k][2];
			}
			outputLayer[i].uploadPrevNodes(prevNodes);
		}
	}

	/**
	 * Runs the calculatious through the network
	 */
	private void calcOutput() {
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 12; j++) {
				System.out.println("Calculating node " + j + " in layer " + i);
				nodes[j][i].calcValue();
				System.out.println("Got " + nodes[j][i].getValue());
			}
		}

		for (int i = 0; i < 3; i++) {
			System.out.println("Calculating node " + i + " in layer 3");
			outputLayer[i].calcValue();
			System.out.println("Got " + outputLayer[i].getValue());
		}
	}

	/**
	 * The backpropagation
	 * 
	 * @param desiredOutput
	 */
	private void calculateChangesToWeights(double[] desiredOutput) {
		calculateError(desiredOutput);
		bpLastLayer(desiredOutput);
		bpSecondLayer(desiredOutput);
		bpFirstLayer(desiredOutput);
	}

	/**
	 * Calculates the new error
	 * 
	 * @param desiredOutput
	 */
	private void calculateError(double[] desiredOutput) {
		System.out.println("Calculating error");
		double tempError = 0;
		for (int i = 0; i < 3; i++) {
			tempError += (outputLayer[i].getValue() - desiredOutput[i]) / desiredOutput[i];
			System.out.println(tempError);
			if (tempError < 0) {
				tempError *= -1;
			}
		}
		latestError = tempError / 3;
	}

	/**
	 * Calculates change in weights in the last layer of weights
	 * 
	 * @param desiredOutput
	 */
	private void bpLastLayer(double[] desiredOutput) {
		for (int j = 0; j < 3; j++) {
			System.out.println("In output " + j + " aiming for " + desiredOutput[j] + " while we got "+ outputLayer[j].getValue() );
			for (int i = 0; i < 12; i++) {
				lastWeightsDelta[i][j] = -stepsize
						* (2 * (outputLayer[j].getValue() - desiredOutput[j]) * nodes[i][2].getValue()) / 3;
				if (outputLayer[j].getPreAcValue() < 0) {
					lastWeightsDelta[i][j] *= leak;
				}
				System.out
						.println("Delta weight from " + i + " to " + j + " in last layer is " + lastWeightsDelta[i][j]);
			}
		}
	}

	/**
	 * Calculates change in weights in the second layer of weights
	 * 
	 * @param desiredOutput
	 */
	private void bpSecondLayer(double[] desiredOutput) {
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				double sum = 0;
				for (int k = 0; k < 3; k++) {
					double descriptiveVariableName = (outputLayer[k].getValue() - desiredOutput[k]) * thirdWeights[j][k];
					if (outputLayer[k].getPreAcValue() < 0) {
						descriptiveVariableName *= leak;
					}
					sum += descriptiveVariableName;
				}
				sum *= (2 * nodes[i][1].getValue() / 3);
				if (nodes[j][2].getPreAcValue() < 0) {
					sum *= leak;
				}
				innerWeightsDelta[i][j][1] = -stepsize * sum;
				System.out.println(
						"Delta weight from " + i + " to " + j + " in second layer is " + innerWeightsDelta[i][j][1]);
			}
		}
	}

	/**
	 * Calculates change in weights in the first layer of weights
	 * 
	 * @param desiredOutput
	 */
	private void bpFirstLayer(double[] desiredOutput) {
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				double sum = 0;
				for (int k = 0; k < 3; k++) {
					double subsum = 0;
					for (int l = 0; l < 12; l++) {
						double part= firstWeights[j][l][1] * thirdWeights[l][k];
						if (nodes[l][2].getPreAcValue() < 0) {
							part *= leak;
						}
						subsum+=part;
					}
					double part = (outputLayer[k].getValue() - desiredOutput[k]) * subsum;
					if (outputLayer[k].getPreAcValue() < 0) {
						part *= leak;
					}
					sum+=part;
				}
				innerWeightsDelta[i][j][0] = -stepsize * sum * 2 * nodes[i][0].getValue() / 3;
				if (nodes[j][1].getPreAcValue() < 0) {
					innerWeightsDelta[i][j][0] *= leak;
				}
				System.out.println(
						"Delta weight from " + i + " to " + j + " in first layer is " + innerWeightsDelta[i][j][0]);
			}
		}
	}

	/**
	 * Adjusts the weights according to the calculations done with the
	 * backpropagation
	 */
	private void applyChangesToWeights() {
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 12; i++) {
				thirdWeights[i][j] += lastWeightsDelta[i][j];
			}
		}
		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 12; j++) {
					firstWeights[i][j][k] += innerWeightsDelta[i][j][k];
				}
			}
		}
	}

	/**
	 * Backpropagation of biases
	 * 
	 * @param desiredOutput
	 */
	private void calculateChangesToBiases(double[] desiredOutput) {
		bpLastLayerNodes(desiredOutput);
		bpThirdLayerNodes(desiredOutput);
		bpSecondLayerNodes(desiredOutput);
	}

	/**
	 * Calculates the changes to the biases of the last layer
	 * 
	 * @param desiredOutput
	 */
	private void bpLastLayerNodes(double[] desiredOutput) {
		for (int i = 0; i < 3; i++) {
			double sum = 0;
			sum = 2 * (outputLayer[i].getValue() - desiredOutput[i]) / 3;
			if (outputLayer[i].getPreAcValue() < 0) {
				sum *= leak;
			}
			deltaBiasLast[i] = -stepsizeB * sum;
			System.out.println("Delta Bias in node " + i + " in the last layer is "+deltaBiasLast[i]);
		}
	}

	/**
	 * Calculates the changes to the biases of the third layer
	 * 
	 * @param desiredOutput
	 */
	private void bpThirdLayerNodes(double[] desiredOutput) {
		for (int i = 0; i < 12; i++) {
			if(nodes[i][2].getValue()!=0) {
				double sum = 0;
				for (int j = 0; j < 3; j++) {
					double part = (outputLayer[j].getValue() - desiredOutput[j]) * thirdWeights[i][j];
					if (outputLayer[j].getPreAcValue() < 0) {
						part *= leak;
					}
					sum+= part;
				}
				deltaBiasFirst[i][2] = (-stepsizeB * 2 * sum) / 3;
				if (nodes[i][2].getPreAcValue() < 0) {
					deltaBiasFirst[i][2] *= leak;
				}
			}else {
				deltaBiasFirst[i][2] = 0;
			}
			System.out.println("Delta Bias in node " + i + " in the third layer is "+deltaBiasFirst[i][2]);
		}
	}

	/**
	 * Calculates the changes to the biases of the second layer
	 * 
	 * @param desiredOutput
	 */
	private void bpSecondLayerNodes(double[] desiredOutput) {
		for (int i = 0; i < 12; i++) {
			if(nodes[i][1].getValue()!=0) {
				double sum = 0;
				for (int j = 0; j < 3; j++) {
					double subsum = 0;
					for (int k = 0; k < 12; k++) {
						double part = thirdWeights[k][j];
						if (nodes[k][2].getPreAcValue() < 0) {
							part *= leak;
						}
						subsum+=part;
					}
					double part = (outputLayer[j].getValue() - desiredOutput[j]) * subsum;
					if (outputLayer[j].getPreAcValue() < 0) {
						part*= leak;
					}
					sum+=part;
				}
				deltaBiasFirst[i][1] = (-stepsizeB * 2 * sum) / 3;
				if (nodes[i][2].getPreAcValue() < 0) {
					deltaBiasFirst[i][1] *= leak;
				}
			}else {
				deltaBiasFirst[i][1] =0;
			}
			System.out.println("Delta Bias in node " + i + " in the second layer is "+deltaBiasFirst[i][1]);
		}
	}

	/**
	 * Applies the calculated changes to biases
	 */
	private void applyChangesToBiases() {
		for (int i = 0; i < 3; i++) {
			outputLayer[i].applyBias(deltaBiasLast[i]);
		}

		for (int i = 0; i < 12; i++) {
			for (int j = 1; j < 3; j++) {
				nodes[i][j].applyBias(deltaBiasFirst[i][j]);
			}
		}
	}

	/**
	 * Returns a string which represents the state of the network
	 * 
	 * @return
	 */
	public String getSaveState() {
		String output = "";
		output += weightResolution + ";";
		output += stepsize + ";";
		output += stepsizeB + ";";
		output += leak + ";";
		output += latestError + ";";
		output += getWeightState();
		output += getBiasState();
		output += ";BIGSPLIT;";
		return output;
	}

	/**
	 * Returns the string which represents the state of the weights
	 * 
	 * @return
	 */
	private String getWeightState() {
		String output = "";
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 12; j++) {
				for (int k = 0; k < 12; k++) {
					output += firstWeights[j][k][i] + ";";
				}
			}
		}
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 3; j++) {
				output += thirdWeights[i][j] + ";";
			}
		}
		return output;
	}

	/**
	 * Returns the string which represents the state of the node biases
	 * 
	 * @return
	 */
	private String getBiasState() {
		String output = "";
		for (int i = 0; i < 12; i++) {
			for (int k = 0; k < 3; k++) {
				output += nodes[i][k].getBias() + ";";
			}
		}
		for (int i = 0; i < 3; i++) {
			output += outputLayer[i].getBias() + ";";
		}
		return output;
	}

	/**
	 * Method to generate a window for progress
	 * 
	 * @param training
	 */

	/**
	 * Takes a string which represents the state of the network, and parses, then
	 * applies, the values out of it
	 * 
	 * @param state
	 */
	public void loadState(String state) {
		final String[] input = state.split(";BIGSPLIT;");
		final String[] split = input[0].split(";");
		weightResolution = Integer.parseInt(split[0]);
		stepsize = Double.parseDouble(split[1]);
		stepsizeB = Double.parseDouble(split[2]);
		leak = Double.parseDouble(split[3]);
		latestError = Double.parseDouble(split[4]);
		int l = 5;
		l = parseWeights(split, l);
		parseBiases(split, l);
	}

	/**
	 * Parses and applies the weights to the network. Type is int to easier keep
	 * track of which parts of the split should be taken by the following functions
	 * 
	 * @param split
	 * @param l
	 * @return
	 */
	private int parseWeights(String[] split, int l) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 12; j++) {
				for (int k = 0; k < 12; k++) {
					firstWeights[j][k][i] = Double.parseDouble(split[l]);
					l++;
				}
			}
		}
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 3; j++) {
				thirdWeights[i][j] = Double.parseDouble(split[l]);
				l++;
			}
		}
		return l;
	}

	/**
	 * Parses the biases from the string in the specified locations
	 * 
	 * @param split
	 * @param l
	 */
	private void parseBiases(String[] split, int l) {
		for (int i = 0; i < 12; i++) {
			for (int k = 0; k < 3; k++) {
				nodes[i][k].setBias(Double.parseDouble(split[l]));
				l++;
			}
		}

		for (int i = 0; i < 3; i++) {
			outputLayer[i].setBias(Double.parseDouble(split[l]));
			l++;
		}
	}
}