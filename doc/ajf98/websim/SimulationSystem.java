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

package doc.ajf98.websim;

import doc.ajf98.websim.objects.*;
import doc.ajf98.websim.processes.DataDump;
import doc.ajf98.websim.processes.abs.RouteableSystemObject;
import doc.ajf98.SimTools.*;

/**
 * This class represents a single simulation system, which
 * can be executed.
 *
 * It is currently not supported to create more than one
 * instance of this object. This is because many of the supporting
 * classes upon which this class depends rely on the ability to
 * create static caches or the like, which will cause indeterminate
 * behaviour if more than one instance of this class is created.
 *
 * @author Andrew Ferrier
 * @version 1.1.4
 */

public class SimulationSystem extends PSim
{
	public SimulationSystem(String name, RouteableSystemObject[] systemObjects, double runTime, int defaultTTL, DataDump dataDump, double resetTime)
	{
		this.name = name;
		this.systemObjects = systemObjects;
		this.runTime = runTime;
		this.dataDump = dataDump;
		this.resetTime = resetTime;
		Message.setDefaultTimeToLive(defaultTTL);
	} 

	public void run() throws InterruptedException
	{
		// Before we execute the simulation, we'll force
		// the Java system to tidy up: this should _help_
		// to keep our numbers consistent.

        System.gc();
		System.runFinalization();

        WebSim.systemStatusTrace("Free VM memory: " + Runtime.getRuntime().freeMemory() + "b");

		for(int i = 0; i < systemObjects.length; i++)
			systemObjects[i].activate();

		dataDump.activate();

		before = System.currentTimeMillis();
		super.execute(new FinishSimulationSystem(before, runTime, resetTime, systemObjects));
		after = System.currentTimeMillis();
	}

	public void printFinalInformation()
	{
        for(int i = 0; i < systemObjects.length; i++)
		{
			System.out.println(systemObjects[i].toFinalString());
		}

		double realSeconds = (after - before) / MS_IN_SECOND;
		double virtualSeconds = PSim.now();
		double speedUp = runTime / realSeconds;

		System.out.println("Simulation took " + WebSim.formatDouble(realSeconds) + "s (real), " + WebSim.formatDouble(virtualSeconds) + "s (virtual) to execute.");
		System.out.println("Speedup of " + WebSim.formatDouble(speedUp) + " over virtual time.");
	}

	public String toString()
	{
		return name;
	}

	public double getRuntime()
	{
		return runTime;
	}

	/**
	 * This class is declared static for performance.
	 */

	private static class FinishSimulationSystem implements Stoppable
	{
		FinishSimulationSystem(long startTime, double runTime, double resetTime, RouteableSystemObject[] systemObjects)
		{
			this.startTime = startTime;
			this.runTime = runTime;
			this.resetTime = resetTime;
			this.systemObjects = systemObjects;
		}

		public boolean stop()
		{
			long realTimeSinceStart = System.currentTimeMillis() - startTime;
			long roundedDifference = realTimeSinceStart - (realTimeSinceStart % NOTIFY_MILLISECONDS);

			if(roundedDifference > previousDifference)
			{
				double virtualTimeRatio = (runTime - PSim.now()) / PSim.now();

				long ETA = (long) (virtualTimeRatio * realTimeSinceStart);

                System.out.println("Still progress, real time " + roundedDifference / MS_IN_SECOND + "s, virtual time " + WebSim.formatDouble(PSim.now()) + "s. ETA " + (ETA / MS_IN_SECOND) + " real s.");

				WebSim.systemStatusTrace("Free VM memory: " + Runtime.getRuntime().freeMemory() + "b");
				
				previousDifference = roundedDifference;
			}

			if(resetTime > 0)
			{
				if(PSim.now() > resetTime && !resetYet)
				{
					for(int i = 0; i < systemObjects.length; i++)
						systemObjects[i].reset();
					resetYet = true;
				}
			}

			/* try
			{
				if(System.in.available() > 0)
				{
					WebSim.warning("Key pressed: aborting simulation.");
					return true;
				}
			}
			catch(java.io.IOException ioe)
			{
			} */
            
			return PSim.now() > runTime;
		}

		private boolean resetYet = false;
		private long previousDifference = 0;
		private long startTime;
		private double runTime;
		private double resetTime;
		private RouteableSystemObject[] systemObjects;
		private static final long NOTIFY_MILLISECONDS = 10 * MS_IN_SECOND;
	}

	private DataDump dataDump;
	private String name;
	private RouteableSystemObject[] systemObjects;
	private double runTime;
	private double resetTime;
	private long before, after;

	private static final int MS_IN_SECOND = 1000;
}
