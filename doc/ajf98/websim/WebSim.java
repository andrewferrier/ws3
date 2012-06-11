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

package doc.ajf98.websim;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import doc.ajf98.SimTools.*;
import doc.ajf98.websim.objects.*;
import doc.ajf98.websim.processes.*;
import doc.ajf98.websim.processes.abs.*;

import java.util.List;

/**
 * This class is the main program which interfaces with the
 * system and the user, to create the WSSS (WS^3) simulation
 * system. It contains the {@link #main(String[])} method.
 *
 * The version of this class is specified in the version JavaDoc
 * tag for this class. The version of the overall program is
 * contained within the static String constant {@link #VERSION}
 * within this class.
 *
 * This class is fairly fragile and must not be moved because
 * many of the other simulation classes depend on it for generalised
 * functions (outputting errors, etc.). This is unfortunate but also
 * generally unavoidable.
 *
 * @author Andrew Ferrier.
 * @version 1.7.4
 */

public class WebSim
{
	/**
	 * The name of the WSSS (WS^3) program.
	 */

	public static final String APPLICATION_NAME = "WSSS (WS^3)";

	/**
	 * The version of the WSSS (WS^3) program.
	 */

	public static final String VERSION = "1.0";

	public static final int TRACE_NONE = 0;
	public static final int TRACE_LITTLE = 1;
	public static final int TRACE_SOME = 2;
	public static final int TRACE_MOST = 3;
	public static final int TRACE_ALL = 4;

	public static int traceLevel = TRACE_LITTLE;

	private static final String TAG_ALPHA = "alpha";
    private static final String TAG_BETA = "beta";
    private static final String TAG_CLIENT = "client";
	private static final String TAG_CONNECTTO = "connectto";
	private static final String TAG_CONSTANT = "constant";
	private static final String TAG_CREATION_DISTRIBUTION = "creationDistribution";
	private static final String TAG_DESTINATION = "destination";
	private static final String TAG_DEST_POSSIBILITY = "destPossibility";
	private static final String TAG_DROP = "drop";
	private static final String TAG_ERLANG = "erlang";   
	private static final String TAG_EXPONENTIAL = "exponential";
	private static final String TAG_GEOMETRIC = "geometric";
	private static final String TAG_K = "k";
	private static final String TAG_LBOUND = "lbound";
	private static final String TAG_MU = "mu";
	private static final String TAG_NAME = "name";
	private static final String TAG_NETWORKNODE = "networknode";
	private static final String TAG_PARETO = "pareto";
	private static final String TAG_POSITIVENORMAL = "positiveNormal";
	private static final String TAG_QUEUE_LENGTH = "queueLength";
	private static final String TAG_RATE = "rate";
	private static final String TAG_ROUTE = "route";                               
	private static final String TAG_ROUTETO = "routeto";
	private static final String TAG_RUNTIME = "runtime";
	private static final String TAG_SERVER = "server";
	private static final String TAG_SERVICE_TIME_DISTRIBUTION = "serviceTimeDistribution";
	private static final String TAG_SIGMA = "sigma";
	private static final String TAG_SYSTEM = "system";
	private static final String TAG_THETA = "theta";
	private static final String TAG_TYPE = "type";
	private static final String TAG_UBOUND = "ubound";
	private static final String TAG_UNIFORM = "uniform";
	private static final String TAG_VALUE = "value";
	private static final String TAG_WEIBULL = "weibull";

	private static final String TAGVALUE_INFINITE = "infinite";

	private static final String ATTRIBUTE_THREADS = "threads";
	private static final String ATTRIBUTE_INSTANCES = "instances";
	private static final String ATTRIBUTE_DEFAULTTTL = "defaultTTL";
	private static final String ATTRIBUTE_DATADUMPPERIOD = "dataDumpPeriod";
	private static final String ATTRIBUTE_TRACELEVEL = "traceLevel";
	private static final String ATTRIBUTE_PROCESSORS = "processors";
	private static final String ATTRIBUTE_THREAD_GRAIN = "threadGrain";
	private static final String ATTRIBUTE_RESETTIME = "resetStatsPoint";

	private static final int DEFAULT_INSTANCES = 1;
	private static final float DEFAULT_DROP = 0;

	private static PrintStream traceFile;

	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("\n" + APPLICATION_NAME + " " + VERSION);
		System.out.println("Copyright (C) Andrew Ferrier (andrew@new-destiny.co.uk) 2002, 2012.");
		System.out.println("See the file LICENCE for licencing information\n");
      
		if (args.length != 2)
		{
			error("You have entered the wrong number of arguments.");
			
			System.out.println("Usage: ");
			System.out.println(" ...websim XMLInputSpec.xml XMLSchema.xsd");

			return;
		}

		File inputFile = new File(args[0]);
		File schemaFile = new File(args[1]);
  			
		System.out.println("Initializing system.");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(true);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);

		try
		{
			dbf.setAttribute("http://apache.org/xml/features/validation/schema", new Boolean(true));
			dbf.setAttribute("http://apache.org/xml/properties/schema/external-schemaLocation", "http://www.new-destiny.co.uk/andrew/project/ " + schemaFile);
		}
		catch(IllegalArgumentException e)
		{
			// This probably means we are using a parser, such as Crimson,
			// which does not support XML Schema validation

			dbf.setValidating(false);
		}

		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(XMLErrorHandler.getXMLErrorHandler());

			diagnostics(dbf, db);

			Document d = parseFile(inputFile, db);

			if(!(XMLErrorHandler.getXMLErrorHandler().abortParse() || d == null))
			{
				System.out.println();
									
				SimulationSystem[] systems = parseDocument(d, inputFile);
		
				for(int i = 0; i < systems.length; i++)
				{
					try
					{
						System.out.println("Running simulation '" + systems[i].toString() + "' for " + formatDouble(systems[i].getRuntime()) + " virtual seconds.");
						systems[i].run();
	
						System.out.println("\nFinal Information:\n");
						systems[i].printFinalInformation();
					}
					catch(Exception e)
					{
						SimProcess.killAll();
						e.printStackTrace();
					}
				}
			}
		}
		catch(ParserConfigurationException pce)
		{
			error("Cannot create XML document parser.");
		}
	}

	private static void diagnostics(DocumentBuilderFactory dbf, DocumentBuilder db)
	{
		if(isSystemSetupTrace())
		{
			System.out.println("DocumentBuilderFactory is ignoring comments: " +
				dbf.isIgnoringComments());
			System.out.println("DocumentBuilderFactory is ignoring element content whitespace: " +
				dbf.isIgnoringElementContentWhitespace());
			System.out.println("DocumentBuilder is namespace aware: " +
				db.isNamespaceAware());
			System.out.println("DocumentBuilder is validating: " +
				db.isValidating());

			DOMImplementation implementation = db.getDOMImplementation();
	
			final String[] features = {"Core", "XML", "Traversal"};
			
			for(int i = 0; i < features.length; i++)
				System.out.println("DOMImplementation supports '" + features[i] + "': " + implementation.hasFeature(features[i], "2.0"));
		}
	}

	/**
	 * Parse the input file.
	 *
	 * @return a DOM Document object which represents the file.
	 */

	private static Document parseFile(File inputFile, DocumentBuilder db)
	{
    	try
		{
			Document d = db.parse(inputFile);
			return d;
		}
		catch(IOException ioe)
		{
			error("I/O problem with input file: Are you sure that:");
			System.out.println("\t1. The file you specified exists?");
			System.out.println("\t2. You have the ability and permissions to read the file?");
			System.out.println("\t3. The file is not open by some other program or person?");
			return null;
		}
		catch(SAXException se)
		{
			error("General file parsing error: " + se.getMessage() + ".");
			return null;
		}
	}

	/**
	 * Parse a whole document. Only supports parsing for one simulation system
	 * currently: more might be supported later, though this in unlikely
	 * because it would require widescale changes to the `static' objects
	 * throughout the program.
	 *
	 * @return an array of SimulationSystem objects.
	 */

	private static SimulationSystem[] parseDocument(Document d, File outputPath)
	{
		NodeList systems = d.getElementsByTagName(TAG_SYSTEM);
		return new SimulationSystem[] { parseSystem((Element) systems.item(0), outputPath) };
	}

	/*
	 * Parse a single system.
	 *
	 * @return a single SimulationSystem object.
	 */

	private static SimulationSystem parseSystem(Element system, File outputPath)
	{
        String name = ((Element) system.getElementsByTagName(TAG_NAME).item(0)).getFirstChild().getNodeValue();
		String runtime = ((Element) system.getElementsByTagName(TAG_RUNTIME).item(0)).getFirstChild().getNodeValue();
		double runtimeDouble = new Double(runtime).doubleValue();
		String defaultTTL = system.getAttribute(ATTRIBUTE_DEFAULTTTL);
		int defaultTTLInt = new Integer(defaultTTL).intValue();
		String dataDumpPeriod = system.getAttribute(ATTRIBUTE_DATADUMPPERIOD);
		double dataDumpPeriodD = new Double(dataDumpPeriod).doubleValue();
		String traceL = system.getAttribute(ATTRIBUTE_TRACELEVEL);
		traceLevel = new Integer(traceL).intValue();
		String resetTime = system.getAttribute(ATTRIBUTE_RESETTIME);
		double resetTimeD = new Double(resetTime).doubleValue();

		File traceFileF, dumpFileF = null;
		PrintStream dataDumpStream;

		try
		{
			traceFileF = new File(outputPath.getParentFile(), name + "_trace.txt");
			traceFile = new PrintStream(new BufferedOutputStream(new FileOutputStream(traceFileF)));
        }
		catch(FileNotFoundException fnfe)
		{
			error("Could not create trace file.");
			return null;
		}

        List processObjects = new LinkedList();
		NodeList systemObjects = system.getChildNodes();

		for(int j = 0; j < systemObjects.getLength(); j++)
		{
			Node systemObject = systemObjects.item(j);

			if(!(systemObject instanceof Element))
				continue;

			String nodeName = systemObject.getNodeName();

			if(nodeName.equals(TAG_CLIENT) || nodeName.equals(TAG_SERVER)
				|| nodeName.equals(TAG_NETWORKNODE))
				processObjects.addAll(parseSystemObject((Element) systemObject));
		}

		RouteableSystemObject[] rso = (RouteableSystemObject[]) processObjects.toArray(new RouteableSystemObject[0]);
        		
		try
		{
			dumpFileF = new File(outputPath.getParentFile(), name + "_dump.csv");
			dataDumpStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(dumpFileF)));
        }
		catch(FileNotFoundException fnfe)
		{
			error("Could not create dump file.");
			return null;
		}

		DataDump dataDump = new DataDump(dataDumpPeriodD, dataDumpStream, rso);

		traceObjectCreation("Simulation system ", name, " created with maximum runtime " + runtimeDouble + ".");

		systemStatusTrace("Outputting to data dump file " + dumpFileF + " every " + dataDumpPeriodD + " virtual seconds.");
		systemStatusTrace("Outputting to trace file " + traceFileF);

		return new SimulationSystem(name, rso, runtimeDouble, defaultTTLInt, dataDump, resetTimeD);
	}

	/**
	 * Parse a system object, which may have multiple instances.
	 *
	 * @return a List of SystemObject objects, one for each instance
	 * of the object.
	 */

	private static List parseSystemObject(Element systemObject)
	{
		String nodeName = systemObject.getNodeName();

		String name = ((Element) systemObject.getElementsByTagName(TAG_NAME).item(0)).getFirstChild().getNodeValue();
        NodeList children = systemObject.getChildNodes();

        int instances = new Integer(systemObject.getAttribute(ATTRIBUTE_INSTANCES)).intValue();

		List systemObjects = new LinkedList();

		for (int i = 0; i < instances; i++)
			systemObjects.add(parseSingleSystemObject(systemObject, nodeName, new SystemObjectID(name, i, instances), children, instances));

		return systemObjects;
	}
	
	private static RouteableSystemObject parseSingleSystemObject(Element systemObject, String nodeName, SystemObjectID soid, NodeList children, int instances)
	{
		DistributionSampler distribution = null;
		List connections = new LinkedList();
		List routes = new LinkedList();
		List destinations = new LinkedList();
		int queueLength = BalkingQueue.INFINITE_SIZE;

		for(int i = 0; i < children.getLength(); i++)
		{
			Node childI = children.item(i);

            if(!(childI instanceof Element))
				continue;

			Element childElement = (Element) childI;
			String childNodeName = childElement.getNodeName();

			if (childNodeName.equals(TAG_CONNECTTO))
				connections.add(parseConnection(soid, childElement));
			else if (childNodeName.equals(TAG_ROUTETO))
				routes.add(parseRoute(soid, childElement));
			else if (childNodeName.equals(TAG_DEST_POSSIBILITY))
				destinations.add(parseDestination(soid, childElement));
			else if (childNodeName.equals(TAG_QUEUE_LENGTH))
			{
				String queueLen = childElement.getFirstChild().getNodeValue();

				if(queueLen.trim().equals(TAGVALUE_INFINITE))
					queueLength =  BalkingQueue.INFINITE_SIZE;
				else
					queueLength = new Integer(queueLen).intValue();
			}
		}

		Connection[] connectionsArray = (Connection[]) connections.toArray(new Connection[0]);
		Route[] routesArray = (Route[]) routes.toArray(new Route[0]);
		SystemObjectID[] destinationsArray = (SystemObjectID[]) destinations.toArray(new SystemObjectID[0]);

		if(nodeName.equals(TAG_CLIENT))
		{
			distribution = parseDistributionSampler((Element) systemObject.getElementsByTagName(TAG_CREATION_DISTRIBUTION).item(0).getFirstChild());

			traceObjectCreation("Client", soid, "Created.");
			return new Client(soid, distribution, connectionsArray, routesArray, destinationsArray);
		}
		else if(nodeName.equals(TAG_SERVER))
		{
			distribution = parseDistributionSampler((Element) systemObject.getElementsByTagName(TAG_SERVICE_TIME_DISTRIBUTION).item(0).getFirstChild());

			int threads = new Integer(systemObject.getAttribute(ATTRIBUTE_THREADS)).intValue();
			int processors = new Integer(systemObject.getAttribute(ATTRIBUTE_PROCESSORS)).intValue();
			double threadGrain = new Double(systemObject.getAttribute(ATTRIBUTE_THREAD_GRAIN)).doubleValue();

			if (processors > threads)
				warning(soid + ": There are more processors defined than threads. Harmless, but pointless.");

			traceObjectCreation("Server", soid, "Created.");
			return new Server(soid, distribution, connectionsArray, routesArray, threads, queueLength, processors, threadGrain);
		}
		else if(nodeName.equals(TAG_NETWORKNODE))
		{
			distribution = parseDistributionSampler((Element) systemObject.getElementsByTagName(TAG_SERVICE_TIME_DISTRIBUTION).item(0).getFirstChild());

            NodeList dropList = systemObject.getElementsByTagName(TAG_DROP);

			float drop = (dropList.getLength() > 0) ?
				new Float(((Element) dropList.item(0)).getFirstChild().getNodeValue()).floatValue() :
				DEFAULT_DROP;

			traceObjectCreation("Network Node", soid, "Created.");
			return new NetworkNode(soid, distribution, connectionsArray, routesArray, drop, queueLength);
		}
		else return null;
	}

    private static Connection parseConnection(SystemObjectID fromSOID, Element node)
	{
        String connectToString = node.getFirstChild().getNodeValue();
		SystemObjectID connectToSOID = new SystemObjectID(connectToString, SystemObjectID.NO_SPECIFIC_INSTANCE);

		traceObjectCreation("Connection", "from " + fromSOID, "Created to object " + connectToSOID + ".");
		return new Connection(connectToSOID);
	}

	private static Route parseRoute(SystemObjectID routeFromSOID, Element node)
	{
		String destinationString = ((Element) node.getElementsByTagName(TAG_DESTINATION).item(0)).getFirstChild().getNodeValue();
		String routeToString = ((Element) node.getElementsByTagName(TAG_ROUTE).item(0)).getFirstChild().getNodeValue();

		SystemObjectID destinationSOID = new SystemObjectID(destinationString, SystemObjectID.NO_SPECIFIC_INSTANCE);
		SystemObjectID routeToSOID = new SystemObjectID(routeToString, SystemObjectID.NO_SPECIFIC_INSTANCE);

		traceObjectCreation("Route", "from " + routeFromSOID, "Route to object " + destinationSOID + " via connection " + routeToSOID + ".");
		return new Route(destinationSOID, routeToSOID);
	}

	private static SystemObjectID parseDestination(SystemObjectID clientName, Element node)
	{
		String destinationString = node.getFirstChild().getNodeValue();

		SystemObjectID destinationSOID = new SystemObjectID(destinationString, SystemObjectID.NO_SPECIFIC_INSTANCE);

        traceObjectCreation("Potential destination", "for " + clientName, "Created as " + destinationSOID + ".");
		return destinationSOID;
	}

	private static DistributionSampler parseDistributionSampler(Element element)
	{
		String elementName = element.getNodeName();

		if(elementName.equals(TAG_CONSTANT))
		{
			double value = new Double(element.getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Constant(value);
		}
		else if(elementName.equals(TAG_EXPONENTIAL))
		{
			double rate = new Double(element.getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Exp(rate);
		}
		else if(elementName.equals(TAG_GEOMETRIC))
		{
			double p = new Double(element.getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Geometric(p);
		}
		else if(elementName.equals(TAG_UNIFORM))
		{
			double lbound = new Double(((Element) element.getElementsByTagName(TAG_LBOUND).item(0)).getFirstChild().getNodeValue()).doubleValue();
			double ubound = new Double(((Element) element.getElementsByTagName(TAG_UBOUND).item(0)).getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Uniform(lbound, ubound);
		}
		else if(elementName.equals(TAG_POSITIVENORMAL))
		{
			double mu = new Double(((Element) element.getElementsByTagName(TAG_MU).item(0)).getFirstChild().getNodeValue()).doubleValue();
			double sigma = new Double(((Element) element.getElementsByTagName(TAG_SIGMA).item(0)).getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.PositiveNormal(mu, sigma);
		}
		else if(elementName.equals(TAG_ERLANG))
		{
            double k = new Double(((Element) element.getElementsByTagName(TAG_K).item(0)).getFirstChild().getNodeValue()).doubleValue();
			double theta = new Double(((Element) element.getElementsByTagName(TAG_THETA).item(0)).getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Erlang(k, theta);
		}
		else if(elementName.equals(TAG_WEIBULL))
		{
            double alpha = new Double(((Element) element.getElementsByTagName(TAG_ALPHA).item(0)).getFirstChild().getNodeValue()).doubleValue();
			double beta = new Double(((Element) element.getElementsByTagName(TAG_BETA).item(0)).getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Weibull(alpha, beta);
		}
		else if(elementName.equals(TAG_PARETO))
		{
            double lambda = new Double(element.getFirstChild().getNodeValue()).doubleValue();
			return new Samplers.Pareto(lambda);
		}
		else
		{
			error("Unsupported distribution type: " + elementName + "\n" +
			      "The entire system object will be ignored.");
			return null;
		}
	}

    // THE FOLLOWING ARE GENERALISED OUTPUT FUNCTIONS

	public static void traceObjectOperation(String objectType, String objectName, String message)
	{
		if(isTraceObjectOperation())
			traceFile.println("@" + formatDouble(PSim.now()) + " " + objectType + " " + objectName + " : " + message);
	}

	public static boolean isTraceObjectOperation()
	{
		return traceLevel >= TRACE_MOST;
	}

	public static void traceObjectOperationDetailed(String objectType, String objectName, String message)
	{
		if(traceLevel >= TRACE_ALL)
			traceObjectOperation(objectType, objectName, message);
	}

	public static void traceObjectOperationDetailed(String objectType, SystemObjectID soid, String message)
	{
        traceObjectOperationDetailed(objectType, soid.toString(), message);
	}

	public static void traceObjectCreation(String objectType, String objectName, String message)
	{
		if(traceLevel >= TRACE_SOME)
			traceFile.println(objectType + " " + objectName + " : " + message);
	}

	public static void traceObjectCreation(String objectType, SystemObjectID soid, String message)
	{
		traceObjectCreation(objectType, soid.toString(), message);
	}

	public static void traceObjectCreationDetailed(String objectType, String objectName, String message)
	{
		if(traceLevel >= TRACE_MOST)
			traceObjectCreation(objectType, objectName, message);
	}

	public static void systemStatusTrace(String message)
	{
		if(isSystemSetupTrace())
			System.out.println(message);
	}

	public static void systemSetupTrace(String message)
	{
		if(isSystemSetupTrace())
			traceFile.println(message);
	}

	public static boolean isSystemSetupTrace()
	{
		return traceLevel >= TRACE_LITTLE;
	}

	public static void error(String errorMessage)
	{
		traceFile.println(errorMessage);
		System.err.println("ERROR: " + errorMessage);
	}

	public static void warning(String warningMessage)
	{
		traceFile.println(warningMessage);
		System.err.println("WARNING: " + warningMessage);
	}

	public static String formatDouble(double d)
	{
        StringBuffer sb = new StringBuffer(20);

		return df.format(d, sb, fp).toString().trim();
	}

	public static String formatFloat(float f)
	{
		return formatDouble((double) f);
	}

    /**
	 * Used by formatDouble().
	 */

	private static final DecimalFormat df = new DecimalFormat("###,###,###,##0.0####");

	/**
	 * Used by formatDouble().
	 */

	private static final FieldPosition fp = new FieldPosition(DecimalFormat.INTEGER_FIELD);
		
	static
	{
		df.setMaximumFractionDigits(5);
	}

	/**
	 * This class uses the singleton design pattern.
	 */

	private static class XMLErrorHandler implements org.xml.sax.ErrorHandler
	{
		static XMLErrorHandler getXMLErrorHandler()
		{
			return instance;
		}

		private XMLErrorHandler()
		{
			super();
		}

		public void error(org.xml.sax.SAXParseException A)
		{
			errorOut(A);
			abortParse = true;
		}

		public void fatalError(org.xml.sax.SAXParseException A)
		{
			errorOut(A);
			abortParse = true;
		}

		public void warning(org.xml.sax.SAXParseException A)
		{
			errorOut(A);
		}

		boolean abortParse()
		{
			return abortParse;
		}

		private void errorOut(SAXParseException spe)
		{
			WebSim.error("Problem parsing input file on line " + spe.getLineNumber() + ", column " + spe.getColumnNumber() + ": " + spe.getMessage());
		}

		private boolean abortParse = false;
		private static XMLErrorHandler instance = new XMLErrorHandler();
	}
}
