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

import java.util.*;

/**
 * Version 1.0 was Tony Field's version.
 *
 * @author Andrew Ferrier
 * @author Tony Field
 * @version 1.2
 */

public class PSim
{
    protected static void schedule(SimProcess p, double t)
	{
		procList.insert(new ProcListEntry(p, t));
	}

	public static double now()
	{
		return time;
	}

	public static void execute(Stoppable b) throws InterruptedException
	{
		while(!b.stop())
		{
			ProcListEntry e = (ProcListEntry) (procList.removeFromFront());
			time = e.wakeTime;
			e.proc.sem.up();
			psem.down();
		}

		SimProcess.allThreads.interrupt();
	}

	/**
	 * I made this class private --- Andrew Ferrier ---
	 * there was no need for it to be otherwise.
	 */

	private static class ProcList extends OrderedList
	{
		public boolean before(Object x, Object y)
		{
			return (((ProcListEntry) x).wakeTime <= ((ProcListEntry) y).wakeTime);
		}
	}

	private static class ProcListEntry
	{
		double wakeTime;
		SimProcess proc;

		public ProcListEntry(SimProcess p, double t)
		{
			wakeTime = t;
			proc = p;
		}
	}

	static Semaphore psem = new Semaphore();
	static double time = 0.0;

	private static ProcList procList = new ProcList();
}
