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
 * Version 1.0 was Tony Field's version. I have
 * made changes very similar to those changes
 * I documented for the Measure class.
 *
 * @author Andrew Ferrier
 * @author Tony Field
 * @version 1.2
 */

public class SystemMeasure
{
	public SystemMeasure()
	{
		this(DEFAULT_MEASURE_SIZE);
	}

	public SystemMeasure(int noMoments)
	{
		if (noMoments < 2)
			noMoments = DEFAULT_MEASURE_SIZE;

		moments = new double[noMoments + 1];

		resetTime = PSim.time;
	}

	public void update(double x)
	{
		for(int i = 0; i < moments.length; i++)
			moments[i] += (Math.pow(current, (double) (i + 1))) * 
						  (PSim.time - lastChange);

		current = x;
		lastChange = PSim.time;
		n++;
	} 

	public long count()
	{
		return n;
	}

	public double currentValue()
	{
		return current;
	}

	public double timeLastChanged()
	{
		return lastChange;
	}

	public double mean()
	{
		return moments[0] / (PSim.time - resetTime);
	}

	public double variance()
	{
		double mean = this.mean();
		return moments[1] / (PSim.time - resetTime) - mean * mean;
	}

	public double moment(int x)
	{
		return moments[x - 1];
	}

	public void reset()
	{
		resetTime = PSim.time;
		n = 0;
		for(int i = 0; i < moments.length; i++)
			moments[i] = 0.0;
	} 

	private double lastChange = 0.0;
	private long n = 0;
	private double moments[];
	private double current = 0.0;
	private double resetTime = 0.0;

	private static final int DEFAULT_MEASURE_SIZE = 2;
}   
