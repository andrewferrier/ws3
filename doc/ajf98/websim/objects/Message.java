/*

Copyright (c) 2002, 2012, Andrew Ferrier. Portions based on work by, and (c) 
Tony Field 2000, 2001. All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are 
met: 

Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
 
Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution. 
 
The name(s) of the author(s) of the software may not be used to endorse or 
promote products derived from this software without specific prior written 
permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package doc.ajf98.websim.objects;

import doc.ajf98.websim.exceptions.*;

/**
 * An instance of this class represents a message in the simulation
 * system from one system object to another.
 *
 * @author Andrew Ferrier
 * @version 1.1.2.1
 */

public abstract class Message
{
	/**
	 * Get a general String representation of this message.
	 *
	 * @return a general String representation of this message.
	 */

	public abstract String toString();

	/**
	 * Create a new message.
	 *
	 * @param source the ID of the source system object.
	 * @param dest the ID of the destination system object.
	 */

	public Message(SystemObjectID source, SystemObjectID dest)
	{
		if(source == null || dest == null)
			throw new IllegalArgumentException("Neither source nor dest can be null.");

		this.source = source;
		this.dest = dest;
		this.timeToLive = defaultTimeToLive;
	}

	/**
	 * Get the ID of the system object which is the source of this message.
	 *
	 * @return the ID of the system object which is the source of this message.
	 */

	public SystemObjectID getSource()
	{
		return source;
	}

	/**
	 * Get the ID of the system object which is the destination of this message.
	 *
	 * @return the ID of the system object which is the destination of this message.
	 */

	public SystemObjectID getDest()
	{
		return dest;
	}

	/**
	 * Inform the message that it just been routed.
	 *
	 * @throws MessageExpiredException if this message has expired because
	 *  it's time-to-live has fallen below zero.
	 */

	public void routed() throws MessageExpiredException
	{
		timeToLive--;

		if(timeToLive < 0)
			throw new MessageExpiredException(this);

		if(defaultTimeToLive <= 0)
			throw new UnsupportedOperationException("Messages cannot be routed until the default TTL has been set.");
	}

	/**
	 * This method should be called to set the initial (default)
	 * time to live for all messages. It affects messages created
	 * from the point it is called onwards.
	 *
	 * @param defaultTimeToLive the default time to live for all messages.
	 */

	public static void setDefaultTimeToLive(int defaultTimeToLive)
	{
		if(defaultTimeToLive <= 0)
			throw new IllegalArgumentException("The default time to live must be greater than zero.");

		Message.defaultTimeToLive = defaultTimeToLive;
	}

	private SystemObjectID source, dest;
	private int timeToLive;
	private static int defaultTimeToLive = -1;
}
