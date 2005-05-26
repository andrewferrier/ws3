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

package doc.ajf98.websim.processes.abs;

import doc.ajf98.SimTools.*;
import doc.ajf98.websim.objects.*;

/**
 * An instance of this object represents an object in the simulation
 * system.
 *
 * However, this object does not have to be globally recognised,
 * an can be private to another SystemObject. As such, it does not have
 * to have a corresponding SystemObjectID.
 *
 * @author Andrew Ferrier
 * @version 1.3
 */

public abstract class SystemObject extends SimProcess
{
	/**
	 * @return a simple string representing this object's
	 * name.
	 */

	public abstract String toString();

	/**
	 * @return a String which can be used to describe the
	 * current state of the object and all it's statistical
	 * measurements.
	 */

	public abstract String toFinalString();

	/**
	 * Accept a message for this object.
	 *
	 * @param message the message to accept.
	 */

	public abstract void messageIn(Message message);

	/**
	 * This should be called be the simulation manager
	 * when equilibrium is reached --- it resets the statistics
	 * for this system object.
	 */

	public abstract void reset();

	/**
	 * @return a short string representing this object's
	 * name.
	 */

	public String getName()
	{
		return toString();
	}
}
