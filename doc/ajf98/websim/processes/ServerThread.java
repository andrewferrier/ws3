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

package doc.ajf98.websim.processes;

import doc.ajf98.SimTools.*;
import doc.ajf98.websim.*;
import doc.ajf98.websim.objects.*;
import doc.ajf98.websim.processes.abs.*;

/**
 * @author Andrew Ferrier
 * @version 0.3
 */

class ServerThread extends SystemObject	implements Comparable
{
	ServerThread(Server ownerServer, int index, DistributionSampler serviceTimeDistribution, double threadGrain)
	{
		this.ownerServer = ownerServer;
		this.index = index;
		this.serviceTimeDistribution = serviceTimeDistribution;
		this.threadGrain = threadGrain;

		this.reset();

		WebSim.traceObjectCreation("Server Thread", getName(), "Created.");
	}

	public void messageIn(Message message)
	{
		WebSim.traceObjectOperation("SThread", getName(), "Has been asked to process " + message + " by " + ownerServer);

		this.message = message;
		in++;
		activate();
	}

	public void runProcess() throws InterruptedException
	{
		while(true)
		{
			busy = true;

			WebSim.traceObjectOperationDetailed("SThread", getName(), "Entering execution loop.");

			double serviceTimeNow = serviceTimeDistribution.next();
			double initialServiceTime = serviceTimeNow;

			while(serviceTimeNow > 0)
			{
				WebSim.traceObjectOperationDetailed("SThread", getName(), "Waiting to get control of a processor.");

				while(ownerServer.serverProcessors <= 0)
					passivate();

				ownerServer.serverProcessors--;
				resource.claim();
				ownerServer.resource.claim();

				WebSim.traceObjectOperationDetailed("SThread", getName(), "Has control of a processor. " + serviceTimeNow + " vsec still remaining for processing.");

				if(serviceTimeNow > threadGrain)
				{
                    hold(threadGrain);
					serviceTimeNow -= threadGrain;
				}
				else
				{
					hold(serviceTimeNow);
					serviceTimeNow = 0;
				}

				resource.release();
				ownerServer.resource.release();
				ownerServer.serverProcessors++;

				WebSim.traceObjectOperationDetailed("SThread", getName(), "Has released a processor.");
			}

			//hold(serviceTimeNow);
            
			serviceTimeMeasure.add(initialServiceTime);
			ownerServer.serviceTimeMeasure.add(initialServiceTime);

			if(message instanceof Request)
			{
				Reply reply = new Reply((Request) message);
				ownerServer.returnReply(reply);
				out++;

				WebSim.traceObjectOperation("SThread", getName(), "Service of " + message + " complete. Sent: " + reply + ".");
			}
			else
			{
				WebSim.warning("There is probably a routing error in your system specification.\n" +
					"Server " + ownerServer.getName() + " has recieved a message that was not a request: " + message + "\n" +
					"It will be ignored. Service complete.");

				faulty++;
			}

			busy = false;

			passivate();
		}
	}

	boolean isExecutingRequest()
	{
		return busy;
	}

	public String toString()
	{
		return ownerServer + "(" + index + ")";
	}

	public String toFinalString()
	{
		String s = "Server Thread " + this + "\n";
		s += "Recieved " + in + " requests, Sent " + out + " replies, " + WebSim.formatFloat(((float) out) / ((float) in) * 100) + "% replies.\n";

		if (faulty > 0)
			s += faulty + " of the messages recieved were faulty.\n";

		s += "Mean Service Time: " + WebSim.formatDouble(serviceTimeMeasure.mean()) + ", Variance of Service Time: " + WebSim.formatDouble(serviceTimeMeasure.variance()) + "\n";
		s += "Utilisation: " + WebSim.formatDouble(resource.utilisation()) + "\n";

		return s;
	}

	public int compareTo(Object o)
	{
		ServerThread st = (ServerThread) o;

		return this.index - st.index;
	}

	public void reset()
	{
		in = out = faulty = 0;
		resource.reset();
		serviceTimeMeasure.reset();
	}

	private Message message;
	private Server ownerServer;
	private DistributionSampler serviceTimeDistribution;
	private long in, out, faulty;
	private int index;
	private boolean busy = false;

	private Resource resource = new Resource();
	private Measure serviceTimeMeasure = new Measure();

	private double threadGrain;
}
