package net.jibini.cortex;

public class SensorLog
{
	public static final int HISTORY_LENGTH = 2048;
	
	public double[] history = new double[HISTORY_LENGTH];
	public int pointer = 0;
	
	public void putValue(double v)
	{
		history[pointer] = v;
		pointer ++;
		pointer %= HISTORY_LENGTH;
	}
	
	public double getAverage(int length)
	{
		double sum = 0;
		for (int i = pointer; i >= pointer - length; i --)
			sum += history[(i < 0) ? HISTORY_LENGTH + i : i];
		return sum / length;
	}
}
