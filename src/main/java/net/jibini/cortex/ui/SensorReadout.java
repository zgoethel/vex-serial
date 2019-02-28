package net.jibini.cortex.ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.jibini.cortex.SensorLog;
import net.jibini.cortex.SensorValues;

public class SensorReadout extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public GridLayout grid;
	public JPanel panel;
	public SensorValues values;
	
	public JLabel[] analog = new JLabel[SensorValues.ANALOG_COUNT];
	public JLabel[] digital = new JLabel[SensorValues.DIGITAL_COUNT];
	public JLabel[] motor = new JLabel[SensorValues.MOTOR_COUNT];
	public JLabel ms = new JLabel(". . .");
	public JLabel perSecond = new JLabel(". . .");
	
	public SensorLog secondLog = new SensorLog();
	long lastUpdate = 0L;

	public SensorReadout(SensorValues values)
	{
		this.setTitle("Sensor Readout");
		this.setSize(750, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		grid = new GridLayout(3, 12);
		panel = new JPanel(grid);
		this.add(panel);

		for (int i = 0; i < SensorValues.MOTOR_COUNT; i ++)
		{
			int rI = i;//SensorValues.MOTOR_COUNT - i - 1;
			panel.add(motor[rI] = new JLabel("N/A"));
			motor[rI].setHorizontalAlignment(JLabel.CENTER);
		}
		
		panel.add(new JLabel());
		panel.add(new JLabel());

		for (int i = 0; i < SensorValues.ANALOG_COUNT; i ++)
		{
			int rI = i;//SensorValues.ANALOG_COUNT - i - 1;
			panel.add(analog[rI] = new JLabel("N/A"));
			analog[rI].setHorizontalAlignment(JLabel.CENTER);
		}
		
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(ms);
		panel.add(perSecond);
		
		for (int i = 0; i < SensorValues.DIGITAL_COUNT; i ++)
		{
			int rI = SensorValues.DIGITAL_COUNT - i - 1;
			panel.add(digital[rI] = new JLabel("N/A"), SwingConstants.CENTER);
			digital[rI].setHorizontalAlignment(JLabel.CENTER);
		}
		
		values.onUpdate.add(this::onUpdate);
		this.values = values;
		lastUpdate = System.nanoTime();
	}
	
	private void onUpdate()
	{
		for (int i = 0; i < SensorValues.ANALOG_COUNT; i ++)
			analog[i].setText(Integer.toString(values.analog[i]));
		for (int i = 0; i < SensorValues.DIGITAL_COUNT; i ++)
			digital[i].setText(Integer.toString(values.digital[i]));
		for (int i = 0; i < SensorValues.MOTOR_COUNT; i ++)
			motor[i].setText(Integer.toString(values.motor[i]));
		
		long diff = System.nanoTime() - lastUpdate;
		lastUpdate = System.nanoTime();
		
		double sec = (double)diff / 1000000000;
		secondLog.putValue(sec);
		double avgSec = secondLog.getAverage(100);
		
		perSecond.setText(String.format("%.0f/sec", 1 / avgSec));
		ms.setText(String.format("%.3fms", avgSec * 1000));
	}
}
