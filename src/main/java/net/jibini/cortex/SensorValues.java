package net.jibini.cortex;

import java.util.ArrayList;
import java.util.List;

public class SensorValues
{
	public static final int MOTOR_COUNT = 10;
	public static final int DIGITAL_COUNT = 12;
	public static final int ANALOG_COUNT = 8;

	public int[] motor = new int[MOTOR_COUNT];
	public int[] digital = new int[DIGITAL_COUNT];
	public int[] analog = new int[ANALOG_COUNT];
	public List<Runnable> onUpdate = new ArrayList<>();
	
	public void handleUpdate(String update)
	{
		boolean malformed = false;
//		System.out.println(update);
		
		try
		{
			if (update.contains(":") && update.contains(";"))
			{
				String[] colonSplit = update.split(":", 2);
				String updateStr = colonSplit[0];
				String valueStr = colonSplit[1].split(";")[0];
				String[] valueStrs = valueStr.split(",");
				
				int[] values = new int[valueStrs.length];
				for (int i = 0; i < valueStrs.length; i ++)
					values[i] = Integer.valueOf(valueStrs[i]);
			
				if (updateStr.equals("A"))
				{
					if (values.length == ANALOG_COUNT)
					{
						for (int i = 0; i < ANALOG_COUNT; i ++)
							analog[i] = values[i];
					} else
						System.out.println("Unexpected analog update count: " + values.length);
					
					for (Runnable run : onUpdate)
						run.run();
				} else if (updateStr.equals("D"))
				{
					if (values.length == DIGITAL_COUNT)
					{
						for (int i = 0; i < DIGITAL_COUNT; i ++)
							digital[i] = values[i];
					} else
						System.out.println("Unexpected digital update count: " + values.length);
				} else if (updateStr.equals("M"))
				{
					if (values.length == MOTOR_COUNT)
					{
						for (int i = 0; i < MOTOR_COUNT; i ++)
							motor[i] = values[i];
					} else
						System.out.println("Unexpected motor update count: " + values.length);
				}
			} else
				malformed = true;
		} catch (NumberFormatException ex)
		{
			malformed = true;
		} finally
		{
			if (malformed)
			{
				System.out.println("Malformed update received:");
				System.out.println(update);
			}
		}
	}
}
