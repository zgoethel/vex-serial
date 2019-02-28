package net.jibini.cortex.port;

import net.jibini.cortex.Cortex;

public abstract class Port
{
	public Cortex cortex;
	public int pin;
	
	public Port(Cortex cortex, int pin)
	{
		this.cortex = cortex;
		this.pin = pin;
	}
	
	public abstract int get();
	
	public abstract char getCommandID();
	
	public void set(int value)
	{
		StringBuilder command = new StringBuilder();
		command.append(getCommandID());
		command.append(pin).append(',');
		command.append(value).append(";\n");
		
		cortex.sendQueue.add(command.toString());
	}
}
