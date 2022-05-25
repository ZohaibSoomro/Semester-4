import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

class MyForm extends JFrame implements KeyListener, ActionListener {

	private Container c;
	private JLabel stname;
	private JLabel rollno;
	private JLabel email;
	private JLabel mobileno;
	private JTextField stnameTF;
	private JTextField rollnoTF;
	private JTextField emailTF;
	private JTextField mobilenoTF;
	private JButton submitBtn;
	private JButton exitBtn;
	private JButton loadDataBtn;
	private JTable dataTable;
	private boolean nameValidated = false;
	private Connection connection = null; 

	public Connection getConnection() {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			return DriverManager.getConnection(
					"jdbc:ucanaccess://C:\\Users\\Zohaib Hassan Soomro\\Downloads\\Assignments\\sda pr\\SDA-Project\\Database\\Students.accdb");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}

	MyForm() {

		c = this.getContentPane();
		c.setLayout(null);

		stname = new JLabel("Student Name ");
		stname.setBounds(50, 50, 120, 30);
		stnameTF = new JTextField();
		stnameTF.setBounds(150, 50, 150, 30);
		stnameTF.addKeyListener(this);

		dataTable = new JTable();

		rollno = new JLabel("Roll No ");
		rollno.setBounds(50, 100, 120, 30);
		rollnoTF = new JTextField();
		rollnoTF.setBounds(150, 100, 150, 30);

		email = new JLabel("Email ");
		email.setBounds(50, 150, 120, 30);
		emailTF = new JTextField();
		emailTF.setBounds(150, 150, 150, 30);

		mobileno = new JLabel("Mobile no ");
		mobileno.setBounds(50, 200, 120, 30);
		mobilenoTF = new JTextField();
		mobilenoTF.setBounds(150, 200, 150, 30);

		submitBtn = new JButton("Submit");
		submitBtn.setBounds(50, 250, 100, 30);

		exitBtn = new JButton("Exit");
		exitBtn.setBounds(200, 250, 100, 30);
		exitBtn.addActionListener(this);

		loadDataBtn = new JButton("Load Data");
		loadDataBtn.setBounds(350, 250, 100, 30);
		loadDataBtn.addActionListener(this);

		c.add(stname);
		c.add(stnameTF);

		c.add(rollno);
		c.add(rollnoTF);

		c.add(email);
		c.add(emailTF);

		c.add(mobileno);
		c.add(mobilenoTF);

		c.add(submitBtn);
		c.add(exitBtn);
		c.add(loadDataBtn);

		submitBtn.addActionListener(this);
		emailTF.addKeyListener(this);

	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent event) {
		int key = event.getKeyChar();
		for (int i = 'A'; i <= 'z'; i++) {
			if (i > 'Z' && i < 'a') {
				continue;
			}
			if (!(key == i)) {
				nameValidated = false;
			} else if (key == i || key == ' ') {
				nameValidated = true;
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// if name is not validated
		String name = "", email = "";
		int rollNum = 0;
		long mobileNum = 0;
		if (e.getSource().equals(submitBtn)) {
			if (!nameValidated) {
				JOptionPane.showMessageDialog(null, "plaease enter a valid name!");
				stnameTF.requestFocus();
			} else {
				name = stnameTF.getText();
				try {
					rollNum = Integer.parseInt(rollnoTF.getText());
					// if email is not valid
					email = emailTF.getText();
					if (!email.contains(".") || !email.contains("@")) {
						JOptionPane.showMessageDialog(null, "Invalid email Address");
						emailTF.requestFocus();
					} else {
						try {
							mobileNum = Long.parseLong(mobilenoTF.getText());
							// if mobile number is not validated
							if (mobilenoTF.getText().length() != 11) {
								JOptionPane.showMessageDialog(null, "Inappropriate amount of digits in mobile number");
								throw new Exception();
							} else {
								mobileNum = Long.parseLong(mobilenoTF.getText());
								try {
									connection = getConnection();
									PreparedStatement pStatement = connection.prepareStatement(
											"insert into Student(Name,RollNo,Email,MobileNo) values(?,?,?,?)");

									pStatement.setString(1, name);
									pStatement.setString(2, Integer.toString(rollNum));
									pStatement.setString(3, email);
									pStatement.setString(4, Long.toString(mobileNum));
									pStatement.execute();
									connection.close();

									JOptionPane.showMessageDialog(null, "Data inserted successfully.");
									clearTextFields();
								} catch (Exception ev) {
									JOptionPane.showMessageDialog(null, ev);
								}
							}
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, "Invalid Mobile Number");
							mobilenoTF.requestFocus();
						}
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Enter valid roll number");
					rollnoTF.requestFocus();
				}
			}

		} // submit button ended
		if (e.getSource().equals(exitBtn)) {
			int answer = JOptionPane.showConfirmDialog(null, "Are you sure to exit?");
			if (answer == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
		if (e.getSource().equals(loadDataBtn)) {
			JFrame dataTableFrame = new JFrame("Data");
			try {
				connection = getConnection();
				PreparedStatement pStmt = connection.prepareStatement("Select * from Student");
				ResultSet set = pStmt.executeQuery();
				dataTable.setModel(DbUtils.resultSetToTableModel(set));
				dataTableFrame.setSize(800, 500);
				dataTableFrame.add(dataTable);
				dataTableFrame.setLocationRelativeTo(null);
				dataTableFrame.setVisible(true);
				dataTableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JOptionPane.showMessageDialog(null, "Data Shown Successfully.");

			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1);
			}
		}
	} // action performed ended

	public void clearTextFields() {
		stnameTF.setText("");
		rollnoTF.setText("");
		mobilenoTF.setText("");
		emailTF.setText("");
	}
}

public class NewRegForm {
	public static void main(String args[]) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			MyForm frame = new MyForm();
			frame.setTitle("Registration Form");
			frame.setVisible(true);
			frame.setSize(500, 500);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}

	}
}