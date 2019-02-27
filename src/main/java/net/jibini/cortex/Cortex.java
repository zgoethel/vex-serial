package net.jibini.cortex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.NRSerialPort;
import net.jibini.cortex.ui.SensorReadout;

public class Cortex
{
	public static Cortex SINGLETON = null;
	
	public SensorReadout sensorReadout;
	public SensorValues sensorValues;
	
	public NRSerialPort serial;
	public BufferedReader input;
	public OutputStream output;
	
	public Cortex(String port, int baudRate)
	{
		sensorValues = new SensorValues();
		sensorReadout = new SensorReadout(sensorValues);
		
		serial = new NRSerialPort(port, baudRate);
		serial.connect();
		input = new BufferedReader(new InputStreamReader(serial.getInputStream()));
		output = serial.getOutputStream();
		
		startInfiniteThread(this::updateInput);
		startInfiniteThread(this::updateOutput);
		
		SINGLETON = this;
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
			byte[] buff = { (byte)0, (byte)1, (byte)10, (byte)(1 - sensorValues.digital[10]), (byte)10 };
			
			for (byte b : buff)
			{
				output.write(b);
				output.flush();
			}
			
			Thread.sleep(333);
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
