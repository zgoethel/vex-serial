package net.jibini.cortex;

import java.util.LinkedList;

@Deprecated
public class Command extends LinkedList<Byte>
{
	private static final long serialVersionUID = 1L;

	public static final byte COMMAND_WRITE = 0;
	public static final byte COMMAND_COMMIT = 10;
	public static final byte COMMAND_WRITE_ANALOG = 2;
	public static final byte COMMAND_WRITE_DIGITAL = 1;
	public static final byte COMMAND_WRITE_MOTOR = 3;
	
	public Command(byte ... values)
	{
		for (byte b : values)
			this.add(b);
	}
	
	public static Command createWrite(byte type, byte pin, byte value)
	{
		return new Command(COMMAND_WRITE, type, pin, value, COMMAND_COMMIT);
	}
}
