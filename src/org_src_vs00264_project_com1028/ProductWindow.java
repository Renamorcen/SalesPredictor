package org_src_vs00264_project_com1028;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.GridLayout;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ProductWindow extends Window{

	private JTable table;
	private ProductWindow thisWindow = this;
	MainWindow parentWindow;
	private Product product;

	/**
	 * Create the application.
	 */
	public ProductWindow(Product product, MainWindow parentWindow) {
		this.parentWindow = parentWindow;
		this.product = product;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setTitle(product.getName());
		frame.setBounds(100, 100, 726, 512);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		table = new JTable();
		updateWindow();
		JScrollPane tablePane = new JScrollPane(table);
		panel.add(tablePane);
		
		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JButton addEntry = new JButton("Add Entry");
		addEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewEntry tempDialogBox = new NewEntry(product, thisWindow);
				tempDialogBox.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tempDialogBox.setVisible(true);
			}
		});
		
		JLabel lblUhohSomethingWent = new JLabel(product.getName());
		buttonPanel.add(lblUhohSomethingWent);
		buttonPanel.add(addEntry);
		
		JButton addLotsOfEntries = new JButton("Add Entries Via File");
		addLotsOfEntries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(frame);
				
				if(fc.getSelectedFile()!=null) {
					product.addFile(fc.getSelectedFile());
				}
				updateWindow();
			}
		});
		buttonPanel.add(addLotsOfEntries);
		
		JButton delEntry = new JButton("Delete Entry");
		delEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				product.deleteEntry(table.getSelectedRow());
				updateWindow();
			}
		});
		
		JButton btnDownloadEntries = new JButton("Download Entries");
		btnDownloadEntries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LinkGeneratorDialog tempDialogBox = new LinkGeneratorDialog();
				tempDialogBox.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tempDialogBox.setVisible(true);
			}
		});
		buttonPanel.add(btnDownloadEntries);
		buttonPanel.add(delEntry);
		
		JButton backBtn = new JButton("Back");
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		
		JButton btnTrainNetwork = new JButton("Train Network");
		btnTrainNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SalesPredictor.trainNetwork(product);
			}
		});
		buttonPanel.add(btnTrainNetwork);
		
		JButton btnProduceNeuralNetwork = new JButton("Produce Neural Network Forecast");
		btnProduceNeuralNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SalesPredictor.produceNeuralNetworkForecast(product);
			}
		});
		buttonPanel.add(btnProduceNeuralNetwork);
		
		JButton btnProduceStatisticalForecast = new JButton("Produce Statistical Forecast");
		btnProduceStatisticalForecast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SalesPredictor.produceStatisticalForecast(product);
			}
		});
		buttonPanel.add(btnProduceStatisticalForecast);
		buttonPanel.add(backBtn);
	}
	
	/**
	 * Method for showing the new window
	 */

	@Override
	public void updateWindow() {
		DefaultTableModel model = new DefaultTableModel(product.getTabledEntryList(),
				new String[] {"Date", "Value"}) {
				Class[] columnTypes = new Class[] {
					String.class, String.class
				};
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			};
		table.setModel(model);
	}

}
