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

package doc.ajf98.SimTools;

/**
 * This class represents an executable simulation process.
 *
 * I consider version 1.0 of this class to be written
 * by Tony Field.
 *
 * @author Andrew Ferrier
 * @author Tony Field
 * @version 1.3
 */

public abstract class SimProcess
{
	public SimProcess()
	{
		(new SimThread()).start();
	}

	/**
	 * Run this simulation process.
	 */

	protected abstract void runProcess() throws InterruptedException;

	/**
	 * Hold this simulation process for t virtual time units.
	 *
	 * @param t the number of virtual time units to hold for.
	 */

    protected void hold(double t) throws InterruptedException
	{
		PSim.schedule(this, PSim.time + t);
		waitToBeWoken();
	}

	protected void passivate() throws InterruptedException
	{
		isActive = false;
		waitToBeWoken();
	}

	public static boolean isActive(SimProcess p)
	{
		return p.isActive;
	}

	public boolean isActive()
	{
		return isActive(this);
	}

	// Note: you can ONLY activate a passivated process...
	// Sleeping processes are active

	public static void activate(SimProcess p)
	{
		p.isActive = true;
		PSim.schedule(p, PSim.time);
	}

	public void activate()
	{
		SimProcess.activate(this);
	}

	private void waitToBeWoken() throws InterruptedException
	{
		PSim.psem.up();
		sem.down(); 
	}

	private void die() throws InterruptedException
	{
		PSim.psem.up();
	}

	/**
	 * @author Andrew Ferrier
	 */

	public static void killAll()
	{
		allThreads.destroy();
	}

	private class SimThread extends Thread
	{
		public SimThread()
		{
			super(allThreads, "SimProcess");
		}

		public void run()
		{
			try
			{
				sem.down();
				runProcess(); 
				die();
			}
			catch(InterruptedException e)
			{
			}
		}
	}

	Semaphore sem = new Semaphore();
	static ThreadGroup allThreads = new ThreadGroup("Processes");

	private boolean isActive = false;
}
