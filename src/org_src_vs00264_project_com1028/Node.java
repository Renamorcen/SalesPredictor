package org_src_vs00264_project_com1028;

import java.util.Random;

/**
 * The node class
 * Could be split up in to the inner nodes, hidden nodes and output nodes.
 * However, this would only add more classes without serving much more of a purpose.
 * @author Vytenis
 *
 */
public class Node {

	/**
	 * The reference to the previous nodes from which to extarct the data
	 */
	private Node[] prevNodes = new Node[12];
	
	/**
	 * The reference to the weights connecting to the previous nodes to calculate the new value
	 */
	private double[] weights = new double[12];
	
	/**
	 * The bias of the neural network
	 */
	double bias;
	
	/**
	 * The resolution with which the bias will be generated
	 */
	int biasResolution = 20;
	
	/**
	 * Values to be held by the node before the activation function
	 */
	double preAcValue;
	
	/**
	 * The value to be held by the node after the activation function
	 */
	double value;
	
	/**
	 * The leak of the activation function
	 */
	double leak = 0;
	
	/**
	 * Whether or not this node has been dropped out
	 */
	boolean dropped = false;
	
	/**
	 * Constructor to generate the random bias and set the leak
	 * @param leak
	 */
	public Node(double leak) {
		Random rand = new Random();
		bias = rand.nextInt(biasResolution)-(biasResolution/2);
		this.leak = leak;
	}
	/**
	 * Getter for the value
	 * @return
	 */
	public double getValue() {
		return this.value;
	}
	
	/**
	 * Getter for the value before activation
	 * @return
	 */
	public double getPreAcValue() {
		return this.preAcValue;
	}
	
	public boolean getDropped() {
		return this.dropped;
	}
	
	/**
	 * Setter for the values. only used for input nodes, but creating a subclass would take up more code and wouldn't do much.
	 * @param value
	 */
	public void loadData(double value) {
		this.value = value;
	}
	
	/**
	 * Loads the weights connecting to the previous nodes
	 * @param weights
	 */
	public void loadWeights(double[] weights) {
		this.weights = weights;
	}
	
	/**
	 * Loads the new bias when loading the program from a file
	 * @param bias
	 */
	public void setBias(double bias) {
		this.bias = bias;
	}
	
	/**
	 * Applies the change in weights after the backpropagation
	 * @param bias
	 */
	public void applyBias(double bias) {
		this.bias += bias;
		System.out.println("New bias: " + this.bias);
	}
	
	/**
	 * Sets the pointers to the previous nodes from which to extract data from during calculation
	 * @param prevNodes
	 */
	public void uploadPrevNodes(Node[] prevNodes) {
		this.prevNodes = prevNodes;
	}
	
	/**
	 * Returns the bias for backpropagation
	 * @return
	 */
	public double getBias() {
		return this.bias;
	}
	
	/**
	 * Calculates the value for this node
	 */
	public void calcValue() {
		if(!dropped) {
			System.out.println("Not-Dropped");
			double weightedSum = 0;
			for(int i = 0; i < 12; i++) {
				weightedSum += prevNodes[i].getValue()*weights[i];
				}
			weightedSum+=bias;
			preAcValue = weightedSum;
			value=activationFunction(weightedSum);
		}
		else {
			System.out.println("Dropped");
			value = 0;
			preAcValue = 0;
			dropped = false;
		}
		
	}
	
	/**
	 * The activation function
	 * @param input
	 * @return
	 */
	private double activationFunction(double input) {
		double output = input;
		if(output < 0) {
			output*=leak;
		}
		return output;
	}
	
	public void dropout() {
		dropped = true;
	}
}
