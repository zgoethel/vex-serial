package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class AnalogPort extends Port
{
	public AnalogPort(Cortex cortex, int pin)
	{
		super(cortex, pin);
	}
	
	public AnalogPort(int pin)
	{
		this(Cortex.SINGLETON, pin);
	}
	
	@Override
	public int get()
	{
		return cortex.sensorValues.analog[pin];
	}

	@Override
	public char getCommandID()
	{
		return 'A';
	}
}
