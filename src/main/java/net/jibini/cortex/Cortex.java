package net.jibini.cortex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import gnu.io.NRSerialPort;
import net.jibini.cortex.port.DigitalPort;
import net.jibini.cortex.ui.SensorReadout;

public class Cortex
{
	public static Cortex SINGLETON = null;
	
	public SensorReadout sensorReadout;
	public SensorValues sensorValues;
	
	public NRSerialPort serial;
	public BufferedReader input;
	public OutputStream output;
	
	public List<String> sendQueue = new CopyOnWriteArrayList<>();
	public long lastBlink = 0L;
	public DigitalPort statusBlink;
	
	public Cortex(String port, int baudRate, boolean showReadout)
	{
		sensorValues = new SensorValues();
		sensorReadout = new SensorReadout(sensorValues);
		sensorReadout.setVisible(showReadout);
		
		serial = new NRSerialPort(port, baudRate);
		serial.connect();
		input = new BufferedReader(new InputStreamReader(serial.getInputStream()));
		output = serial.getOutputStream();
		
		startInfiniteThread(this::updateInput);
		startInfiniteThread(this::updateOutput);
		
		SINGLETON = this;
		statusBlink = new DigitalPort(10);
	}
	
	public Cortex(String port, int baudRate)
	{
		this(port, baudRate, false);
	}
	
	private void updateInput()
	{
		try
		{
			String update = input.readLine();
			sensorValues.handleUpdate(update);
		} catch (Exception ex)
		{
			if (!ex.getMessage().equals("Underlying input stream returned zero bytes"))
				ex.printStackTrace();
		}
	}
	
	private void updateOutput()
	{
		try
		{
			if (System.currentTimeMillis() - lastBlink > 333)
			{
				statusBlink.set(1 - statusBlink.get());
				lastBlink = System.currentTimeMillis();
			}
			
			for (String command : sendQueue)
			{
				for (byte b : command.getBytes())
				{
					output.write(b);
					output.flush();
					Thread.sleep(1);
				}
				
				sendQueue.remove(command);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void startInfiniteThread(Runnable run)
	{
		new Thread(() -> 
		{ 
			while (true)
			{
				run.run();
				Thread.yield();
			}
		}).start();
	}
}
