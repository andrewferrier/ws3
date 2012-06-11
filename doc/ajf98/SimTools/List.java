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

package doc.ajf98.SimTools;

public class List
{
	private ListNode firstNode;
	private ListNode lastNode;
	private String name ;

	public List( String s )
	{
		name = s;
		firstNode = lastNode = null;
	}

	public List()
	{
		this( "list" );
	}                             

	public Object first()
	{
		if(isEmpty())
			throw new EmptyListException( name );
		else
			return firstNode.data ;
	}

	public void insertAtFront( Object insertItem )
	{
		if(isEmpty())
			firstNode = lastNode = new ListNode( insertItem );
		else
			firstNode = new ListNode( insertItem, firstNode );
	}

	public Object last()
	{
		if(isEmpty())
			throw new EmptyListException( name );
		else
			return lastNode.data ;
	}

	public void insertAtBack( Object insertItem )
	{
		if(isEmpty())
			firstNode = lastNode = new ListNode( insertItem );
		else
			lastNode = lastNode.next = new ListNode( insertItem );
	}

	public Object removeFromFront() throws EmptyListException
	{
		Object removeItem = null;

		if(isEmpty())
			throw new EmptyListException( name );

		removeItem = firstNode.data;  

		if(firstNode.equals( lastNode ))
			firstNode = lastNode = null;
		else
			firstNode = firstNode.next;

		return removeItem;  
	}

	public Object removeFromBack() throws EmptyListException
	{
		Object removeItem = null;

		if(isEmpty())
			throw new EmptyListException(name);

		removeItem = lastNode.data;  

		if(firstNode.equals( lastNode ))
			firstNode = lastNode = null;
		else
		{
			ListNode current = firstNode;

			while(current.next != lastNode)
				current = current.next; 

			lastNode = current;
			current.next = null;
		}

		return removeItem;
	}

	public boolean isEmpty()
	{
		return firstNode == null;
	}

	public ListIterator getIterator()
	{
		return new ListIterator() ;
	}

	public class ListIterator
	{
		private ListNode p, q ;

		public ListIterator()
		{
			p = null ;
			q = firstNode ;
		}

		public boolean canAdvance()
		{
			return !( q == null ) ;
		}

		public void advance() throws EmptyListException {
			if(q == null)
				throw new EmptyListException( name ) ;
			p = q ;
			q = q.next ;
		}

		public Object getValue() throws EmptyListException {
			if(q == null)
				throw new EmptyListException( name ) ;
			return q.data ;
		}

		public void add( Object o )
		{
			ListNode newNode = new ListNode( o, q ) ;
			if(( p == null ) && ( q == null ))
			{
				firstNode = lastNode = newNode ;
				q = newNode ;
			}
			else
				if(p == null)
			{
				firstNode = newNode ;
				p = newNode ;
			}
			else
			{
				p.next = newNode ;
				p = newNode ; 
				if(q == null)
					lastNode = newNode ;
			}
		}  

		/* public void remove() throws EmptyListException {
			if(q == null)
				throw new EmptyListException( name ) ;
			p.next = q.next ;
			q = q.next ;
		} */  
	}

	private class ListNode
	{
		ListNode( Object o )
		{
			this( o, null );
		}

		ListNode( Object o, ListNode nextNode ) 
		{
			data = o;         
			next = nextNode;  
		}

		Object getObject()
		{
			return data;
		}

		ListNode getNext()
		{
			return next;
		}

		Object data;    
		ListNode next;
	}
}
