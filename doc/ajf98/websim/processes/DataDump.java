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

import java.io.PrintStream;
import java.util.*;

import doc.ajf98.websim.WebSim;
import doc.ajf98.websim.processes.abs.*;
import doc.ajf98.SimTools.*;

/**
 * An instance of this class will periodically dump statistical data
 * from the simulation to an output stream, during execution of the simulation.
 *
 * @author Andrew Ferrier
 * @version 1.2.1
 */

public class DataDump extends SimProcess
{
	protected void runProcess() throws InterruptedException
	{
        java.util.List printList = new ArrayList();

		printList.add("v-time");

		for(int i = 0; i < rso.length; i++)
		{
			if(rso[i] instanceof HasQueue)
				printList.add(rso[i].getName() + "QueueLength");

			if(rso[i] instanceof HasUtilisation)
				printList.add(rso[i].getName() + "Utilisation");
		}

		printLine(out, printList);

		while(true)
		{
			hold(period);

            WebSim.traceObjectOperation("DataDump", "DataDump", "Dumping");

			printList.clear();
			printList.add(PSim.now() + "");
            
			for(int i = 0; i < rso.length; i++)
			{
				if (rso[i] instanceof HasQueue)
					printList.add("" + ((HasQueue) rso[i]).getQueueLength());

				if (rso[i] instanceof HasUtilisation)
					printList.add("" + ((HasUtilisation) rso[i]).getUtilisation());
			}

			printLine(out, printList);
		}
	}

	private static void printLine(PrintStream out, java.util.List toPrint)
	{
		Iterator i = toPrint.iterator();

		boolean hadPrevious = false;

		while(i.hasNext())
		{
			if(hadPrevious)
				out.print("," + i.next());
			else
			{
				out.print(i.next());
				hadPrevious = true;
			}
		}

		out.print("\n");

		// If the output stream is not flushed, I have seen nondeterministic
		// bugs pop up where the output stream file is not always complete.

		out.flush();

	}

	/**
	 * @param period period in virtual seconds.
	 * @param out the stream to dump data to.
	 * @param rso an array of all the RouteableSystemObjects in the simulation system.
	 *	that should be dumped.
	 */

	public DataDump(double period, PrintStream out, RouteableSystemObject[] rso)
	{
		this.out = out;
		this.period = period;
		this.rso = rso;
	}

	/**
	 * Ensures that the underlying PrintStream object is closed. This probably
	 * happens anyway, we just want to make sure.
	 */

	public void finalize()
	{
		out.close();
	}

	private double period;
	private PrintStream out;
	private RouteableSystemObject[] rso;
}
