package org_src_vs00264_project_com1028;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DoneDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblStatus;
	/**
	 * Create the dialog.
	 */
	public DoneDialog() {
		setTitle("Please Wait...");
		setBounds(100, 100, 300, 100);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblStatus = new JLabel("Please Wait");
			contentPanel.add(lblStatus);
		}
	}

	public void flip() {
		lblStatus.setText("Done");
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("Done");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			okButton.setActionCommand("Done");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
	}
}
