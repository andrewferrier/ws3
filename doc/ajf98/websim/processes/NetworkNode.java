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
 * @version 1.3
 */

public class NetworkNode extends RouteableSystemObject implements HasQueue, HasUtilisation
{
	public NetworkNode(SystemObjectID soid, DistributionSampler serviceTimeDist, Connection[] connections, Route[] routes, float drop, int queueSize)
	{
		super(soid, connections, routes);
		this.serviceTimeDist = serviceTimeDist;
		this.drop = drop;
		this.q = queueSize == (BalkingQueue.INFINITE_SIZE) ? new Queue() : new BalkingQueue(queueSize);
		this.reset();
	}

	public void runProcess() throws InterruptedException
	{
        while(true)
		{
			WebSim.traceObjectOperation("Network Node", getName(), "Looking at queue.");
			
			if(!q.isEmpty())
			{
				WebSim.traceObjectOperationDetailed("Network Node", getName(), "Discovered message " + q.front() + " at the head of the queue. Starting processing.");

				Message message = (Message) q.dequeue();

				double serviceTimeNow = serviceTimeDist.next();

				u.claim();
                hold(serviceTimeNow);
				u.release();

				serviceTimeMeasure.add(serviceTimeNow);

				if(random.nextFloat() > drop)
				{
					Connection c = findRoute(message);

					if(c != null)
					{
						c.sendMessageVia(message);
						out++;
						WebSim.traceObjectOperationDetailed("Network Node", getName(), "Processing " + message + " complete. Sent on via " + c + ".");
					}
				}
				else
				{
					WebSim.traceObjectOperationDetailed("Network Node", getName(), "Processing " + message + " complete. Randomly dropped message.");
					dropped++;
				}
			}
			else
			{
				WebSim.traceObjectOperationDetailed("Network Node", getName(), "Nothing to process. Going to sleep.");

				// Wait until a message arrives.

				passivate();
			}
		}
	}

	public void messageIn(Message message)
	{
		try
		{
			q.enqueue(message);
			in++;
			if (!isActive()) activate();
		}
		catch(QueueFullException qfe)
		{
            WebSim.traceObjectOperation("NetworkNode", this.toString(), "Dropping " + message + " because input queue is full.");
			dropped++;
		}
	}

	public String toFinalString()
	{
		String s = "Network Node " + getName() + "\n";
		s += "Recieved " + in + " messages, sent on " + out + " messages\n";

		if (dropped > 0)
			s += "Also dropped " + dropped + " messages due to queue overrun or random 'deliberate' drop.\n";

		s += WebSim.formatFloat(((float) out) / ((float) in) * 100) + "% of messages successfully queued were sent on.\n";
		s += "Current Queue Length: " + WebSim.formatDouble(q.queueLength()) + "\n";
		s += "Mean Queue Length: " + WebSim.formatDouble(q.meanQueueLength()) + "\n";
		s += "Mean Time in Queue: " + WebSim.formatDouble(q.meanTimeInQueue()) + "\n";
		s += "Mean Service Time: " + WebSim.formatDouble(serviceTimeMeasure.mean()) + ", Variance of Service Time: " + WebSim.formatDouble(serviceTimeMeasure.variance()) + "\n";
		s += "Utilisation: " + WebSim.formatDouble(u.utilisation()) + "\n";
			
		return s;
	}

	public int getQueueLength()
	{
		return q.queueLength();
	}

	public double getUtilisation()
	{
		return u.utilisation();
	}

	public void reset()
	{
		in = out = dropped = 0;
		u.reset();
		serviceTimeMeasure.reset();
	}
                        
	private DistributionSampler serviceTimeDist;
	private Queue q;
	private Measure serviceTimeMeasure = new Measure();
	private Resource u = new Resource();
	private float drop;
	private long in;
	private long out;
	private long dropped;
}   
