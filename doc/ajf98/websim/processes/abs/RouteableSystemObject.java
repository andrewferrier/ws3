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

import java.util.*;

import doc.ajf98.websim.*;
import doc.ajf98.websim.objects.*;

/**
 * An instance of this class represents an object in the simulation
 * system which is globally visible and which can perform routing.
 *
 * RouteableSystemObjects have a global
 * {@link doc.ajf98.websim.objects.SystemObjectID}
 * which represents them in the system. They also have message I/O
 * capabilities.
 *
 * @author Andrew Ferrier
 * @version 1.1.
 */

public abstract class RouteableSystemObject extends SystemObject
{
	/**
	 * Create a new RouteableSystemObject.
	 *
	 * @param soid the {@link doc.ajf98.websim.objects.SystemObjectID} which represents this instance.
	 * @param connections the connections outgoing from this object.
	 * @param routes the routes for this object.
	 */
	 
	public RouteableSystemObject(SystemObjectID soid, Connection[] connections, Route[] routes)
	{
		this.soid = soid;
		systemMap.put(soid, this);

		// Form the routing map

		WebSim.traceObjectCreation("Routing map", "for " + getName(), "Being formed.");

		routeMap = new HashMap(connections.length + routes.length, (float) 1);

		for(int i = 0; i < connections.length; i++)
		{
			routeMap.put(connections[i].getTo(), connections[i]);
			
			WebSim.traceObjectCreationDetailed("Routing map", "for " + getName(), "Adding element with target: " + connections[i].getTo() + ", connection: " + connections[i]);
		}
                                                              
		for (int i = 0; i < routes.length; i++)
		{
            SystemObjectID connection = routes[i].getViaRoute();

			for (int j = 0; j < connections.length; j++)
			{
				if(connections[j].getTo().equals(connection))
				{
					routeMap.put(routes[i].getDestination(), connections[j]);

					WebSim.traceObjectCreationDetailed("Routing map", "for " + getName(), "Adding element with target: " + routes[i].getDestination() + ", connection: " + connections[j]);
				}
			}
		}

		routeMap = Collections.unmodifiableMap(routeMap);
	}

    /**
	 * Find the best route from this System object to the
	 * target destination of the specified message.
	 *
	 * @return A connection for this object which will get
	 * one closer to the desired destination for the message.
	 * THIS METHOD CAN RETURN NULL SO THIS MUST BE CHECKED FOR.
	 */

	public Connection findRoute(Message m)
	{
		Connection c;

		WebSim.traceObjectOperationDetailed("Object", this.soid, "Attempting to find route for message " + m);

		if (m.getDest().specificInstance())
		{
			// Will not be in routing table

			SystemObjectID generalDestination = (SystemObjectID) m.getDest().clone();
            generalDestination.setInstance(SystemObjectID.NO_SPECIFIC_INSTANCE);

			Connection generalConnection;

			if(routeMap.containsKey(generalDestination))
				generalConnection = (Connection) ((Connection) routeMap.get(generalDestination)).clone();
			else
			{
				WebSim.warning("No route found to " + generalDestination);
				return null;
			}

			if (m.getDest().equalsInName(generalConnection.getTo()))
				generalConnection.getTo().setInstance(m.getDest().getInstance()); 

			c = generalConnection;
		}
		else
		{
			// Will be in routing table

			c = (Connection) routeMap.get(m.getDest());
		}

		if (c == null)
		{
			WebSim.warning("No route found to " + m.getDest());
			return null;
		}
		else
			return c;
	}

	/**
	 * Get the RouteableSystemObject object corresponding to the SystemObjectID
	 * provided.
	 */

	public static RouteableSystemObject getSystemObject(SystemObjectID soidToGet)
	{
		if (soidToGet.specificInstance())
			return (RouteableSystemObject) systemMap.get(soidToGet);
		else
			return (RouteableSystemObject) systemMap.get(soidToGet.returnRandomInstance());
	}

	public SystemObjectID getSOID()
	{
		return soid;
	}

    public String toString()
	{
		return soid.toString();
	}

    private SystemObjectID soid;
	private Map routeMap;

	private static final Map systemMap = new HashMap();

	/**
	 * Can be used for generating random numbers.
	 */

	protected static final doc.ajf98.util.Random random =
			doc.ajf98.util.Random.getRandom();
}
