package org_src_vs00264_project_com1028;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LinkGeneratorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtTicker;
	private JComboBox jcbYearsAgo;
	private JTextField txtGeneratedURL;

	/**
	 * Create the dialog.
	 */
	public LinkGeneratorDialog() {
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			txtTicker = new JTextField();
			txtTicker.setText("Ticker");
			contentPanel.add(txtTicker);
			txtTicker.setColumns(10);
			
			jcbYearsAgo = new JComboBox();
			jcbYearsAgo.setModel(new DefaultComboBoxModel(DateAgo.values()));
			contentPanel.add(jcbYearsAgo);
			
			txtGeneratedURL = new JTextField();
			txtGeneratedURL.setText("URL to be generated");
			contentPanel.add(txtGeneratedURL);
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						int years = jcbYearsAgo.getSelectedIndex()+1;
						String link = SalesPredictor.downloadEntries(txtTicker.getText(), years);
						txtGeneratedURL.setText(link);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
