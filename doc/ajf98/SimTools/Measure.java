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
 * Version 1.0 was Tony Field's version. I made changes to
 * this class to support long counts for the number of
 * moments and tidied up the code, improving the memory
 * usage (the initialisation of the moment array was rather
 * naive). It also appeared to have a bug in the 1-arg constructor
 * where the parameter was essentially ignored, which I fixed.
 *
 * @author Andrew Ferrier
 * @author Tony Field
 * @version 1.1
 */

// IMPLEMENTATION NOTE: THE (i - 1)th array element represents the
// ith moment.

public class Measure
{
	public Measure()
	{
		this(DEFAULT_MEASURE_SIZE);
	}

	public Measure(int m)
	{
		if(m < 0)
			m = DEFAULT_MEASURE_SIZE;

		moment = new double[m];
	}

	public void add(double x)
	{
		for(int i = 0; i < moment.length; i++)
			moment[i] += Math.pow(x, (double) (i + 1));

		n++;
	}

	public double mean()
	{
		return moment[0] / n;
	}

	public long count()
	{
		return n;
	}

	public double variance()
	{
		double mean = this.mean();
		return (moment[1] - n * mean * mean) / (n - 1);
	}

	public double moment(int x)
	{
		return moment[x - 1];
	}

	public void reset()
	{
		n = 0;

		for(int i = 0; i < moment.length; i++)
			moment[i] = 0.0;
	}

	private static final int DEFAULT_MEASURE_SIZE = 2;
	private long n = 0;
	private double moment[];
}
