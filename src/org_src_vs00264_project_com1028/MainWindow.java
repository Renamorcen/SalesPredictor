package org_src_vs00264_project_com1028;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

/**
 * The main window of the program
 * @author Vytenis
 *
 */
public class MainWindow extends Window{

	
	/**
	 * Reference to this window to pass on to the subWindows
	 */
	MainWindow thisWindow = this;
	
	/**
	 * Default value for the selected product ID so that it could ask the user to chose a product
	 */
	int selectedProductId = -1;
	
	/**
	 * All of the swing components
	 */
	
	private JPanel buttonPanel;
	private JButton delProductBtn;
	private JButton btnProdDetails;
	private JButton btnForecasts;
	private JButton btnSave;
	private JButton btnLoad;
	
	/**
	 * Create the application.
	 */
	public MainWindow() {
		super();
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		/**
		 * Initialising the SalesPredictor object
		 */
		
		frame = new JFrame();
		frame.setTitle("Sales Predictor by Vytenis Sliogeris");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel listPanel = new JPanel();
		frame.getContentPane().add(listPanel);
		
		productList = new JList();
		listPanel.add(productList);
		productList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				selectedProductId = productList.getSelectedIndex();
			}
		});
		
		updateWindow();
		buttonPanel = new JPanel();
		frame.getContentPane().add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JButton addProductBtn = new JButton("Add Product");
		addProductBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddProductDialog tempDialogBox = new AddProductDialog(thisWindow);
				tempDialogBox.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tempDialogBox.setVisible(true);
			}
		});
		buttonPanel.add(addProductBtn);
		
		delProductBtn = new JButton("Delete Product");
		delProductBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedProductId==-1) {
					NoItemSelectedErrorWindow tempDialogBox = new NoItemSelectedErrorWindow();
					tempDialogBox.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					tempDialogBox.setVisible(true);
				}else {
					SalesPredictor.deleteProduct(selectedProductId);
				}
			}
		});
		buttonPanel.add(delProductBtn);
		
		btnProdDetails = new JButton("Product Details");
		btnProdDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedProductId==-1) {
					NoItemSelectedErrorWindow tempDialogBox = new NoItemSelectedErrorWindow();
					tempDialogBox.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					tempDialogBox.setVisible(true);
				}else {
					ProductWindow temp = new ProductWindow(SalesPredictor.getProductAtId(selectedProductId), thisWindow);
					temp.showWindow();
				}
			}
		});
		buttonPanel.add(btnProdDetails);
		
		btnForecasts = new JButton("Forecasts");
		btnForecasts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ForecastListWindow temp = new ForecastListWindow(thisWindow);
				temp.showWindow();
			}
		});
		buttonPanel.add(btnForecasts);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SalesPredictor.save();
			}
		});
		buttonPanel.add(btnSave);
		
		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(frame);
				
				if(fc.getSelectedFile()!=null) {
					SalesPredictor.loadState(fc.getSelectedFile());
				}
			}
		});
		buttonPanel.add(btnLoad);
	}
}
