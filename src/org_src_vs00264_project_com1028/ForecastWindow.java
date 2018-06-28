package org_src_vs00264_project_com1028;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ForecastWindow extends Window{

	private Forecast forecast;

	/**
	 * Create the application.
	 */
	public ForecastWindow(Forecast forecast) {
		this.forecast = forecast;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		
		String statString = "Something is wrong";
		if(forecast.getStatisticity()) {
			statString = "Statistical";
		}else {
			statString = "Neural";
		}
		
		frame.setTitle(forecast.getProduct().getName()+ " " + forecast.getDate() + " " + statString);
		frame.setBounds(100, 100, 450, 341);
		//frmPlaceholder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lbDate = new JLabel(forecast.getDate().toString());
		panel.add(lbDate);
		
		JLabel lbProduct = new JLabel(forecast.getProduct().getName());
		panel.add(lbProduct);
		
		double[] input = forecast.getInput();
		double[] output = forecast.getOutput();
		
		JLabel lblPart1 = new JLabel(Double.toString(input[0]));
		panel.add(lblPart1);
		
		JLabel lblPart2 = new JLabel(Double.toString(input[1]));
		panel.add(lblPart2);
		
		JLabel lblPart3 = new JLabel(Double.toString(input[2]));
		panel.add(lblPart3);
		
		JLabel lblPart4 = new JLabel(Double.toString(input[3]));
		panel.add(lblPart4);
		
		JLabel lblPart5 = new JLabel(Double.toString(input[4]));
		panel.add(lblPart5);
		
		JLabel lblPart6 = new JLabel(Double.toString(input[5]));
		panel.add(lblPart6);
		
		JLabel lblPart7 = new JLabel(Double.toString(input[6]));
		panel.add(lblPart7);
		
		JLabel lblPart8 = new JLabel(Double.toString(input[7]));
		panel.add(lblPart8);
		
		JLabel lblPart9 = new JLabel(Double.toString(input[8]));
		panel.add(lblPart9);
		
		JLabel lblPart10 = new JLabel(Double.toString(input[9]));
		panel.add(lblPart10);
		
		JLabel lblPart11 = new JLabel(Double.toString(input[10]));
		panel.add(lblPart11);
		
		JLabel lblPart12 = new JLabel(Double.toString(input[11]));
		panel.add(lblPart12);
		
		JLabel lblPart13 = new JLabel(Double.toString(output[0]));
		lblPart13.setForeground(Color.red);
		panel.add(lblPart13);
		
		JLabel lblPart14 = new JLabel(Double.toString(output[1]));
		lblPart14.setForeground(Color.red);
		panel.add(lblPart14);
		
		JLabel lblPart15 = new JLabel(Double.toString(output[2]));
		lblPart15.setForeground(Color.red);
		panel.add(lblPart15);
		
		JPanel infoPanel = new JPanel();
		frame.getContentPane().add(infoPanel);
		
		String statisticity = "Something went wrong";
		
		if(forecast.getStatisticity()) {
			statisticity = "Statistical method";
		}else {
			statisticity = "Neural Network method";
		}
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel(statisticity);
		infoPanel.add(lblNewLabel);
		
		String confirmed = "Something went wrong";
		
		if(forecast.getConfirmed()) {
			confirmed = "Forecast confirmed";
		}else {
			confirmed= "Forecast yet to be confirmed";
		}
		
		JLabel lblNewLabel_1 = new JLabel(confirmed);
		infoPanel.add(lblNewLabel_1);
		
		
		
		JLabel lblNewLabel_2 = new JLabel("The error is "+ Double.toString((forecast.getConfidence())));
		infoPanel.add(lblNewLabel_2);
		
		JButton btnBack = new JButton("Back");
		infoPanel.add(btnBack);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}
}
