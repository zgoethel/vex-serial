package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class AnalogPort extends Port
{
	public AnalogPort(Cortex cortex, byte pin)
	{
		super(cortex, Port.PortType.ANALOG, pin);
	}
	
	public AnalogPort(byte pin)
	{
		this(Cortex.SINGLETON, pin);
	}
}
