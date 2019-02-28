package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class MotorPort extends Port
{
	public MotorPort(Cortex cortex, int pin)
	{
		super(cortex, pin);
	}
	
	public MotorPort(int pin)
	{
		this(Cortex.SINGLETON, pin);
	}
	
	@Override
	public int get()
	{
		return cortex.sensorValues.motor[pin];
	}

	@Override
	public char getCommandID()
	{
		return 'M';
	}

	@Override
	public void set(int value)
	{
		super.set(value + 128);
	}
}
