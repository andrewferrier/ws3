/*

Copyright (c) 2002, Andrew Ferrier. Portions based on work by, and (c) 
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

import doc.ajf98.websim.*;
import doc.ajf98.websim.exceptions.*;
import doc.ajf98.websim.processes.abs.RouteableSystemObject;

/**
 * An instance of this class represents a one-way connection from
 * one system object to another.
 * 
 * However, the 'from' object is implicit: only the target of the
 * connection is stored within the Connection object.
 * 
 * @author Andrew Ferrier
 * @version 1.1.1
 */

public class Connection	implements Cloneable
{
	/**
	 * Create a new Connection to a SystemObject.
	 *
	 * @param the ID of the {@link doc.ajf98.websim.processes.abs.SystemObject}
	 * 	which the connection should link to.
	 */

	public Connection(SystemObjectID to)
	{
        this.to = to;
	}

	/**
	 * Send a message via this connection.
	 *
	 * @param message the message to send.
	 */

	public void sendMessageVia(Message message)
	{
		try
		{
			message.routed();
			RouteableSystemObject.getSystemObject(to).messageIn(message);
		}
		catch(MessageExpiredException mee)
		{
			WebSim.warning(mee.getLocalizedMessage());
		}
	}

	/**
	 * Return a clone of this Connection. Also clones the underlying
	 * target ID, and is hence a deep clone, in violation of the contract
	 * for this method as specified in {@link java.lang.Object#clone()}.
	 * This should be changed.
	 */

	public Object clone()
	{
		return new Connection((SystemObjectID) to.clone());
	}                           

	public SystemObjectID getTo()
	{
		return to;
	}

	public String toString()
	{
		return "Connection to " + to;
	}

	private SystemObjectID to;
}
