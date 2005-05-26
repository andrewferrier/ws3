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

import doc.ajf98.SimTools.exceptions.*;

/**
 * This class was changed as per the JavaDoc comments.
 * I changed the class to use the java.util.LinkedList
 * class rather than the one Tony Field wrote, to increase
 * speed and portability.
 *
 * I consider version 1.0 to be Tony Field's version.
 *
 * @author Andrew Ferrier
 * @author Tony Field
 * @version 1.1.1
 */

public class Queue
{
	public int queueLength()
	{
		return q.size();
	}

	/**
	 * @throws QueueFullException never. This exception throwing declaration
	 *	is present merely to ensure that subclasses can throw this exception.
	 */

	public void enqueue(Object o) throws QueueFullException
	{
		q.add(new QueueEntry(o));
		popMeasure.update((float) q.size());
	}

	public Object dequeue()
	{
		QueueEntry e = (QueueEntry) q.removeFirst();
		popMeasure.update((float) q.size());
		responseTimeMeasure.add(PSim.time - e.entryTime);
		return e.entry;
	}

	public Object front()
	{
		return((QueueEntry) q.getFirst()).entry;
	}

	public boolean isEmpty()
	{
		return q.isEmpty();
	}

	public double meanQueueLength()
	{
		return popMeasure.mean();
	}

	public double varQueueLength()
	{
		return popMeasure.variance();
	}

	public double meanTimeInQueue()
	{
		return responseTimeMeasure.mean();
	}

	public void reset()
	{
		responseTimeMeasure.reset();
		popMeasure.reset();
	}

	/**
	 * I made this class private --- there was no need
	 * for it to be friendly. Ditto the static modifier.
	 */

	private static class QueueEntry
	{
		double entryTime;
		Object entry;

		public QueueEntry(Object o)
		{
			entryTime = PSim.time;
			entry = o;
		}
	}

	private Measure responseTimeMeasure = new Measure();
	private SystemMeasure popMeasure = new SystemMeasure();

	private LinkedList q = new LinkedList();
}
