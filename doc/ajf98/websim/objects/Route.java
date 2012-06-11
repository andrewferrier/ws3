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

/**
 * An instance of this class represents a route between two
 * system objects, via another system object.
 *
 * The source object ID is NOT stored within instances of this class.
 * It is implicit.
 *
 * @author Andrew Ferrier
 * @version 1.1
 */

public class Route
{
	/**
	 * Create a new Route.
	 *
	 * @param destination the ID of the system object that is the destination
	 *  of this route.
	 * @param viaRoute the ID of the system object that is the next step
	 *  on the route to the destination.
	 */

	public Route(SystemObjectID destination, SystemObjectID viaRoute)
	{
		this.destination = destination;
		this.viaRoute = viaRoute;
	}

	/**
	 * Get the ID of the system object which is the destination of this route.
	 * 
	 * @return the ID of the system object which is the destination of this route.
	 */

	public SystemObjectID getDestination()
	{
		return destination;
	}

	/**
	 * Get the ID of the system object which is the next step on the route.
	 *
	 * @return the ID of the system object which is the next step on the route.
	 */

	public SystemObjectID getViaRoute()
	{
		return viaRoute;
	}

	private SystemObjectID destination;
	private SystemObjectID viaRoute;
}
