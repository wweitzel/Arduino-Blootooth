import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fazecast.jSerialComm.SerialPort;


public class BluetoothLED {
	
	static SerialPort chosenPort;
	static int x = 0;
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		window.setTitle("Arduino LCD Clock");
		window.setSize(400, 120);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Creating drop-down box
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JButton ledButton = new JButton("On");
		ledButton.setPreferredSize(new Dimension(70, 40));
		JPanel topPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		centerPanel.add(ledButton);
		window.add(topPanel, BorderLayout.NORTH);
		window.add(centerPanel, BorderLayout.CENTER);
		
		// Populating drop down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		portList.addItem("Select Port...");
		for (int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		// Configuring the connect button and use another thread to listen for data
		connectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if (chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
						
						// Create a new thread for sending data to arduino
						Thread thread = new Thread() {
							@Override public void run() {
								// wait after connecting, so the bootloader can finish
								try {Thread.sleep(100);} catch (InterruptedException e) {}
								
								PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
								ledButton.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										if (ledButton.getText().equals("On")) {
											ledButton.setText("Off");
											output.print(2);
											output.flush();
										} else {
											ledButton.setText("On");
											output.print(1);
											output.flush();
											
										}
										
										
									}
									
								});
								/*
								// enter infinite loop that sends text to the arduino
								while(true) {
									//output.print(new SimpleDateFormat("hh:mm:ss a     MMMMMM dd, yyyy").format(new Date()));
									Scanner input = new Scanner(System.in);
									System.out.print("Enter 0 or 1: ");
									int num = input.nextInt();
									output.print(num);
									output.flush();
									try {Thread.sleep(100);} catch (InterruptedException e) {}
								}
								*/
							}
						};
						thread.start();
					}
				} else {
					// disconnect from the serial port
					
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
				}
			}
		});
		
		
		window.setVisible(true);
		
		
		

	}

}
