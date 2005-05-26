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

package doc.ajf98.websim.objects;

import java.util.*;

import doc.ajf98.websim.*;

/**
 * Represents a system object with an 'ID'. The way that this ID is
 * represented is as a combination of a name and an instance number.
 * This class provides a convienient mechanism for manipulating these
 * IDs.
 * 
 * @author Andrew Ferrier
 * @version 1.1.1.1
 */

public class SystemObjectID
{
	/**
	 * The magic instance number representing no specific instance.
	 */

	public static final int NO_SPECIFIC_INSTANCE = -1;

	/**
	 * Create a new system object ID with the specified name and
	 * instance number. Also specify the total number of instances
	 * for this name (will be saved in a static cache).
	 *
	 * @param name the name for this object ID.
	 * @param instance the instance number for this object ID.
	 * @param totalInstancesInt the total number of instances for this object name.
	 */

	public SystemObjectID(String name, int instance, int totalInstancesInt)
	{
		this.name = name;
		this.instance = instance;
		totalInstances.put(name, new Integer(totalInstancesInt));
	}

	/**
	 * Create a new system object ID with the specified name and
	 * instance number.
	 *
	 * @param name the name for this object ID.
	 * @param instance the instance number for this object ID.
	 */

	public SystemObjectID(String name, int instance)
	{
		this.name = name;
		this.instance = instance;
	}

	/**
	 * Determine whether this ID represents a specific instance of
	 * an object or not.
	 *
	 * @return true if this is a specific instance, false otherwise.
	 */

	public boolean specificInstance()
	{
		return instance != NO_SPECIFIC_INSTANCE;
	}

	/**
	 * Get the name associated with this object ID.
	 *
	 * @return the name associated with this object ID.
	 */

	public String getName()
	{
		return name;
	}

	/**
	 * Get the instance number associated with this object ID.
	 *
	 * @return the instance number associated with this object ID.
	 */

	public int getInstance()
	{
		return instance;
	}

	/**
	 * Get a String which represents the name and instance number stored
	 * in this ID object.
	 *
	 * @return a String which represents the name and instance number stored
	 *  in this ID object.
	 */

	public String toString()
	{
		if (specificInstance())
			return name + "[" + instance + "]";
		else
			return name + "[?]";
	}

	/**
	 * Clone this SystemObjectID object. This is a shallow clone.
	 *
	 * @return a new SystemObjectID object.
	 */

	public Object clone()
	{
		return new SystemObjectID(name, instance);
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * Determine complete equality between this object and another object.
	 * 
	 * @param o the object to check equality against
	 * @return true if o and this object are both SystemObjectIDs and represent
	 *	the same system object and the same instance of that system object,
	 *  false otherwise.
	 */

	public boolean equals(Object o)
	{
		if (o instanceof SystemObjectID)
		{
			SystemObjectID soid = (SystemObjectID) o;

			if ((soid.name.equals(this.name)) && (soid.instance == this.instance))
				return true;
		}

		return false;
	}

	/**
	 * Determine partial equality between this SystemObjectID object and another 
	 * SystemObjectID object.
	 *
	 * @param soid the SystemObjectID object to check equality against.
	 * @param true if both objects represent the same system object (not necessarily
	 *	the same instance), false otherwise.
	 */

	public boolean equalsInName(SystemObjectID soid)
	{
		return soid.name.equals(this.name);
	}

	/**
	 * Set the instance number for this object.
	 *
	 * @param instance the instance number to set this object to.
	 */

	public void setInstance(int instance)
	{
		this.instance = instance;
	}

	/**
	 * Get a random instance of the system object that this ID refers to.
	 *
	 * @return a SystemObjectID which represents a random instance (uniformly
	 *	distributed) of the system object which this SystemObjectID refers to.
	 */

	public SystemObjectID returnRandomInstance()
	{
		int totalInstancesInt = ((Integer) totalInstances.get(name)).intValue();
        return new SystemObjectID(name, random.nextInt(totalInstancesInt));
	}

	private String name;
	private int instance;

	private static final Map totalInstances = new HashMap();
	private static final doc.ajf98.util.Random random = doc.ajf98.util.Random.getRandom();
}
