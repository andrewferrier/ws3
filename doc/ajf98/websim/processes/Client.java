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

package doc.ajf98.websim.processes;

import java.util.*;

import doc.ajf98.SimTools.*;
import doc.ajf98.websim.*;
import doc.ajf98.websim.objects.*;
import doc.ajf98.websim.processes.abs.RouteableSystemObject;

/**
 * Represents a client in the simulation system.
 * 
 * @author Andrew Ferrier
 * @version 0.2.2
 */

public class Client extends RouteableSystemObject
{
    public Client(SystemObjectID soid, DistributionSampler interarrivalTime, Connection[] connections, Route[] routes, SystemObjectID[] destinations)
	{
		super(soid, connections, routes);
		this.interarrivalTime = interarrivalTime;
		this.destinations = destinations;
		this.reset();
	}

	public void runProcess() throws InterruptedException
	{
        while(true)
		{
			hold(interarrivalTime.next());

			SystemObjectID destination = destinations[random.nextInt(destinations.length)];

			String fileName = WebSim.isTraceObjectOperation() ? 
				"/testFile_" + random.nextInt(FILE_RANDOM_NAME_EXCLUSIVEMAX) + ".html" :
				"/testFile_uncalculated.html";

			Request r = new Request(fileName, this.getSOID(), destination);
			
			WebSim.traceObjectOperation("Client", getName(), "Created " + r + ": finding route.");

			Connection c = findRoute(r);

			if (c != null)
			{
                WebSim.traceObjectOperation("Client", getName(), "Route found. Sending " + r + " via " + c);
                c.sendMessageVia(r);
				out++;
			}
		}
	}

	public void messageIn(Message m)
	{
		if(m instanceof Reply)
		{
			if(m.getDest().equals(this.getSOID()))
			{
				WebSim.traceObjectOperation("Client", getName(), "Recieved reply " + m);
	
				in++;
				replies.add(((Reply) m).requestTimeToNow());
			}
			else
			{                                                  
				WebSim.warning("There is probably a routing error in your system specification.\n" +
					"Client " + getName() + " has recieved a reply destined for  " + m.getDest() + "\n" +
					"It will be ignored.");

				faultyIn++;
			}
		}
		else if(m instanceof Refusal)
		{
			WebSim.traceObjectOperation("Client", getName(), "Recieved refusal " + m);
			refusals++;
		}
		else
		{   
			WebSim.warning("WARNING: There is probably a routing error in your system specification.\n" +
				"Client " + getName() + " has recieved the message  " + m + "\n" +
				"It will be ignored.");
			
			faultyIn++;
		}
	}

	public String toFinalString()
	{
		String s = "Client " + getName() + "\n";
		s += "Sent " + out + " requests, Recieved " + in + " replies, " + WebSim.formatFloat(((float) in) / ((float) out) * 100) + "% replies.\n";

		if (faultyIn > 0)
			s += "Also recieved " + faultyIn + " faulty messages.\n";

		if (refusals > 0)
			s += "Also recieved " + refusals + " refusals.\n";

		s += "Average response time: " + WebSim.formatDouble(replies.mean()) + ", variance in response time: " + WebSim.formatDouble(replies.variance()) + "\n";

		return s;
	}        

	public void reset()
	{
		in = faultyIn = out = refusals = 0;
		replies.reset();
	}

	private DistributionSampler interarrivalTime;
	private SystemObjectID[] destinations;

	private final static short FILE_RANDOM_NAME_EXCLUSIVEMAX = Short.MAX_VALUE;

	private long in;
	private long faultyIn;
	private long out;
	private long refusals;

	private Measure replies = new Measure();
}
