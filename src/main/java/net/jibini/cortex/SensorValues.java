package net.jibini.cortex;

import java.util.ArrayList;
import java.util.List;

public class SensorValues
{
	public static final int MOTOR_COUNT = 10;
	public static final int DIGITAL_COUNT = 12;
	public static final int ANALOG_COUNT = 8;

	public byte[] motor = new byte[MOTOR_COUNT];
	public byte[] digital = new byte[DIGITAL_COUNT];
	public byte[] analog = new byte[ANALOG_COUNT];
	public List<Runnable> onUpdate = new ArrayList<>();
	
	//TODO: String parse byte values; possible error when byte == 10 (value of '\n')
	public void handleUpdate(String update)
	{
		if (update.contains(":") && update.contains(";"))
		{
			String[] colonSplit = update.split(":", 2);
			String updateStr = colonSplit[0];
			String valueStr = colonSplit[1];
			byte[] values = valueStr.getBytes();
			
			if (updateStr.equals("A"))
			{
				if (values.length == ANALOG_COUNT + 1)
				{
					for (int i = 0; i < ANALOG_COUNT; i ++)
						analog[i] = values[i];
				} else
					System.out.println("Unexpected analog update count: " + values.length);
				
				for (Runnable run : onUpdate)
					run.run();
			} else if (updateStr.equals("D"))
			{
				if (values.length == DIGITAL_COUNT + 1)
				{
					for (int i = 0; i < DIGITAL_COUNT; i ++)
						digital[i] = values[i];
				} else
					System.out.println("Unexpected digital update count: " + values.length);
			} else if (updateStr.equals("M"))
			{
				if (values.length == MOTOR_COUNT + 1)
				{
					for (int i = 0; i < MOTOR_COUNT; i ++)
						motor[i] = values[i];
				} else
					System.out.println("Unexpected motor update count: " + values.length);
			}
		} else
		{
			System.out.println("Malformed update received:");
			System.out.println(update);
		}
	}
}
