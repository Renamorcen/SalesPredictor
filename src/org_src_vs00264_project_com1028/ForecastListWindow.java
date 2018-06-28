package org_src_vs00264_project_com1028;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;

public class ForecastListWindow extends Window {

	private MainWindow parentWindow;
	private JTable table;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JButton btnDetails;
	private JPanel panel_2;
	private JButton btnDelete;

	/**
	 * Create the application.
	 */
	public ForecastListWindow(MainWindow parentWindow) {
		this.parentWindow = parentWindow;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Forecasts");
		frame.setBounds(100, 100, 582, 479);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		
		table = new JTable();
		updateWindow();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JScrollPane tablePane = new JScrollPane(table);
		panel.add(tablePane);
		
		panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		btnDetails = new JButton("Details");
		btnDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ForecastWindow temp = new ForecastWindow(SalesPredictor.getForecastAtIndex(table.getSelectedRow()));
				temp.showWindow();
			}
		});
		panel_2.add(btnDetails);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SalesPredictor.deleteForecast(table.getSelectedRow());
				updateWindow();
			}
		});
		panel_2.add(btnDelete);
		
		btnNewButton = new JButton("Back");
		panel_2.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}

	@Override
	public void updateWindow() {
		DefaultTableModel model = new DefaultTableModel(SalesPredictor.getTabledForecastList(),
				new String[] {"Date", "Product"}) {
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
