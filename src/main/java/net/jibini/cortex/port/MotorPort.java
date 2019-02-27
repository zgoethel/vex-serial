package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class MotorPort extends Port
{
	public MotorPort(Cortex cortex, byte pin)
	{
		super(cortex, Port.PortType.MOTOR, pin);
	}
	
	public MotorPort(byte pin)
	{
		this(Cortex.SINGLETON, pin);
	}
}
