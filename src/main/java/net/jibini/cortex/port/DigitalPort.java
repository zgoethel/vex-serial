package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public class DigitalPort extends Port
{
	public DigitalPort(Cortex cortex, byte pin)
	{
		super(cortex, Port.PortType.DIGITAL, pin);
	}
	
	public DigitalPort(byte pin)
	{
		this(Cortex.SINGLETON, pin);
	}
}
