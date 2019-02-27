package net.jibini.cortex.port;

import net.jibini.cortex.Command;
import net.jibini.cortex.Cortex;

public class Port
{
	public static enum PortType
	{
		ANALOG, DIGITAL, MOTOR;
	}
	
	public Cortex cortex;
	public PortType type;
	public byte pin;
	
	public Port(Cortex cortex, PortType type, byte pin)
	{
		this.cortex = cortex;
		this.type = type;
		this.pin = pin;
	}
	
	public byte getValue()
	{
		switch (type)
		{
		case ANALOG:
			return cortex.sensorValues.analog[pin];
		case DIGITAL:
			return cortex.sensorValues.digital[pin];
		case MOTOR:
			return cortex.sensorValues.motor[pin];
		default:
			return 0;
		}
	}
	
	public byte getWriteType()
	{
		switch (type)
		{
		case ANALOG:
			return Command.COMMAND_WRITE_ANALOG;
		case DIGITAL:
			return Command.COMMAND_WRITE_DIGITAL;
		case MOTOR:
			return Command.COMMAND_WRITE_MOTOR;
		default:
			return 0;
		}
	}
	
	public void setValue(byte value)
	{
		Command writeCommand = Command.createWrite(getWriteType(), pin, value);
	}
}
