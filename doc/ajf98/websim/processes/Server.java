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
import doc.ajf98.SimTools.exceptions.*;
import doc.ajf98.websim.*;
import doc.ajf98.websim.objects.*;
import doc.ajf98.websim.processes.abs.*;

/**
 * @author Andrew Ferrier
 * @version 0.2.4
 */

public class Server extends RouteableSystemObject implements HasQueue, HasUtilisation
{
	public Server(SystemObjectID soid, DistributionSampler serviceTimeDist, Connection[] connections, Route[] routes, int threads, int queueSize, int processors, double threadGrain)
	{
		super(soid, connections, routes);
		this.serviceTimeDist = serviceTimeDist;
		this.threads = threads;
		this.q = queueSize == (BalkingQueue.INFINITE_SIZE) ? new Queue() : new BalkingQueue(queueSize);
		this.serverProcessors = processors;

		this.reset();

		for(int i = 0; i < threads; i++)
		{
			ServerThread st = new ServerThread(this, i, serviceTimeDist, threadGrain);
			serverThreads.add(st);
		}
	}

	public void messageIn(Message message)
	{
		try
		{
			WebSim.traceObjectOperationDetailed("Server", this.toString(), "Asked to process message " + message);

			q.enqueue(message);
			in++;
			if (!isActive()) activate();
		}
		catch(QueueFullException qfe)
		{
			WebSim.traceObjectOperation("Server", this.toString(), "Sending refuse for " + message + " because input queue is full.");

			Refusal refusal = new Refusal(message);
			Connection c = findRoute(refusal);

			if(c != null)
			{
				c.sendMessageVia(refusal);
                refused++;
			}
		}
	}
                                        
	void returnReply(Reply r)
	{
		Connection c = findRoute(r);

		if (c != null)
		{
			c.sendMessageVia(r);
			out++;
		}	

		// In case there are messages waiting in the queue

		if(!isActive())
			activate();
	}

	public String toFinalString()
	{
        String s = "Server " + getName() + "\n";
		s += "Recieved " + in + " requests, Sent " + out + " replies\n" + WebSim.formatFloat(((float) out) / ((float) in) * 100) + "% of messages successfully queued were replied to.\n";
		
		if (faultyIn > 0)
			s += "Also recieved " + faultyIn + " faulty messages.\n";

		if (refused > 0)
			s += "Also refused " + refused + " messages due to queue overrun.\n";
        
		s += "Current Queue Length: " + q.queueLength() + "\n";
		s += "Mean Queue Length: " + WebSim.formatDouble(q.meanQueueLength()) + "\n";
		s += "Mean Time in Queue: " + WebSim.formatDouble(q.meanTimeInQueue()) + "\n";
		s += "Mean Service Time: " + WebSim.formatDouble(serviceTimeMeasure.mean()) + ", Variance of Service Time: " + WebSim.formatDouble(serviceTimeMeasure.variance()) + "\n";
		s += "Utilisation: " + WebSim.formatDouble(resource.utilisation()) + "\n";

		// The following call to sort ensures that server threads
		// are printed in ascending numerical order: they implement
		// the Comparable interface.

		Collections.sort(serverThreads);

		Iterator i = serverThreads.iterator();

		while(i.hasNext())
			s += "\n" + ((ServerThread) i.next()).toFinalString();

		return s;
	}

	/*
	 * All that the server process loop is responsible for is finding a free
	 * server thread (if there is one), assigning the message to it, then
	 * sleeping again. THERE ARE NO STATS DISTRIBUTIONS INVOLVED.
	 */

	public void runProcess() throws InterruptedException
	{
    	while(true)
		{
			WebSim.traceObjectOperation("Server", this.toString(), "Looking at queue.");

			//Iterator i = getListIterator();

			Iterator i = serverThreads.iterator();

			while(i.hasNext() && (!q.isEmpty()))
			{
                ServerThread st = (ServerThread) i.next();

				if (!st.isExecutingRequest())
				{
					Message m = (Message) q.dequeue();
											
					WebSim.traceObjectOperationDetailed("Server", this.toString(), "Handing over message " + m + " to " + st);
					
					st.messageIn(m);
				}
			}

			WebSim.traceObjectOperation("Server", this.toString(), "Going to sleep.");
			passivate();
		}
	}

	public int getQueueLength()
	{
		return q.queueLength();
	}

	public double getUtilisation()
	{
		return resource.utilisation();
	}

	public void reset()
	{
		Iterator i = serverThreads.iterator();

		while(i.hasNext())
		{
			ServerThread st = (ServerThread) i.next();
            st.reset();
		}

		faultyIn = in = out = refused = 0;
		resource.reset();
		serviceTimeMeasure.reset();
	}

	private java.util.List serverThreads = new ArrayList();
	
	private DistributionSampler serviceTimeDist;
	private Queue q;

	private long faultyIn;
	private long in;
	private long out;
	private long refused;
	private int threads;

	// These items have package-private access

	Resource resource = new Resource();
	Measure serviceTimeMeasure = new Measure();
	int serverProcessors;
}
