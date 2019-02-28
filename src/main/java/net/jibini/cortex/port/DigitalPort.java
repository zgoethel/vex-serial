package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class DigitalPort extends Port
{
	public DigitalPort(Cortex cortex, int pin)
	{
		super(cortex, pin);
	}
	
	public DigitalPort(int pin)
	{
		this(Cortex.SINGLETON, pin);
	}
	
	@Override
	public int get()
	{
		return cortex.sensorValues.digital[pin];
	}

	@Override
	public char getCommandID()
	{
		return 'D';
	}
}
