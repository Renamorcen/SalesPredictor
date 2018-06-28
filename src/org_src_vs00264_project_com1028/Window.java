package org_src_vs00264_project_com1028;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.table.DefaultTableModel;

/**
 * The class which holds the methods that repeat over a few windows
 * @author Vytenis
 *
 */
public class Window {
	
	/**
	 * Product List to access all of the products
	 */
	protected JList productList;
	
	/**
	 * The frame of the window
	 */
	protected JFrame frame;
	
	public void updateWindow() {
		String[] values = SalesPredictor.getProductNames();
		productList.setModel(new AbstractListModel() {
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
	}
	
	public void showWindow() {
		frame.setVisible(true);
	}
}
