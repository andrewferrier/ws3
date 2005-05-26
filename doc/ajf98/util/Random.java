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

package doc.ajf98.util;

import java.util.*;

/**
 * This class is modelled around the standard Java API
 * class java.util.Random. It is a wrapper which provides
 * additional functionality.
 *
 * @author Andrew Ferrier
 * @version 0.2
 */

public class Random
{
	private Random()
	{
	}

	public double nextDouble()
	{
		//iterate();
		//return ((double) x) / ((double) MAX_LONG);

		return javaRandom.nextDouble();
	}

	public float nextFloat()
	{
		return javaRandom.nextFloat();
	}

	public int nextInt(int ubound)
	{
		return javaRandom.nextInt(ubound);
	}
		
	public long nextLong()
	{
		return javaRandom.nextLong();
	}

	/* private void iterate()
	{
		x = (MULTIPLIER * x + ADDITIVE) % MAX_LONG;
	} */

	public static doc.ajf98.util.Random getRandom()
	{
		return instance;
	}

	private final java.util.Random javaRandom = new java.util.Random(0);

	private static final doc.ajf98.util.Random instance
		= new doc.ajf98.util.Random();

	/* private long x = 0x0ABCDEFAAAAAAAAAL;

	private static final long MULTIPLIER = 5;
	private static final long ADDITIVE = 3;
	private static final int LONG_BITS = 60;
	private static final long MAX_LONG = (long) Math.pow(2, LONG_BITS); */
}
