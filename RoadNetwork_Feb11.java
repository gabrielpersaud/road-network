// RoadNetwork.java
// author Doug Jones
// version 2019-02-11

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

/** Error reporting package
 *  Provide a standard prefix and behavior for error reporting
 *  @author Douglas W. Jones
 *  @version 2019-01-21
 */
class Errors {
    /** Prefix string for error messages
     */
    private static String prefix = "??: ";

    /** Set prefix on error reports, should be done before any error reports
     *  @arg p the prefix on any error messages
     */
    public static void setPrefix( String p ) {
	prefix = p;
    }

    /** Report nonfatal errors, output a message and return
     *  @arg m the message to output
     */
    public static void warn( String m ) {
	System.err.println( prefix + ": " + m );
    }
}

/** Roads are one-way paths between Intersections
 *  @author Douglas Jones
 *  @version 2019-02-06
 *  @see Intersection
 */
class Road {
    private float travelTime; // how long it takes to get to the other end
    private Intersection destination; // where does this road go
    private Intersection source;      // where does this road come from
    // Bug Need attributes of a road

    /** construct a new Road
     *  @param sc the scanner used to get the attributes of this road
     *  When called, the keyword "road" has already been scanned,
     *  so we are ready to scan the source and destination plus other stuff.
     */
    public Road( Scanner sc ) {
	String srcName = null; // default if there is no next
	String dstName = null;
	travelTime = 0.0F;     // default if no delay provided
	if (sc.hasNext()) srcName = sc.next();
	if (sc.hasNext()) dstName = sc.next();
	// names are defined here but may be null if missing
	source = RoadNetwork.findIntersection( srcName );
	destination = RoadNetwork.findIntersection( dstName );

	// it is legal to call toString now because all fields are initialized

	// deal with errors
	if (source == null) {
	    Errors.warn( "" + this + "ill defined source" );
	}
	if (destination == null) {
	    Errors.warn( "" + this + "ill defined destination" );
	}

	// deal with delay
	if (sc.hasNextFloat()) {
	    travelTime = sc.nextFloat();
	} else {
	    // default value remains unchanged
	    Errors.warn( "" + this + " delay expected" );
	}
    }

    public String toString() {
	// prepare for missing fields of a badly declared intersection
	String src = "???";
	String dst = "???";

	// find real values
	if ((source != null) && (source.name != null)) {
	    src = source.name;
	}
	if ((destination != null) && (destination.name != null)) {
	    src = destination.name;
	}
        return "Road " + src + ' ' + dst + ' ' + travelTime;
    }
}

/** Intersections are joined by Roads
 *  @author Douglas Jones
 *  @version 2019-02-06
 *  @see Road
 */
class Intersection {
    /** The name of this interseciton
      */
    public String name; // the interesection's name or null if broken

    // where this intersection connects
    LinkedList <Road> outgoing = new LinkedList <Road> ();
    // Bug do we need to know what roads lead here?

    // Bug how about different kinds of intersections

    /** construct a new Intersection
     *  @param sc the scanner used to get the attributes of this intersection
     *  When called, the keyword "intersection" has already been scanned,
     *  so we are ready to scan the additional attributes, if any.
     */
    public Intersection( Scanner sc ) {
	if (sc.hasNext()) {
	    name = sc.next();
	} else {
	    Errors.warn( "Intersection has missing name" );
	    name = null;
	}
	if (RoadNetwork.findIntersection( name ) != null) {
	    Errors.warn( "Name reused for intersection " + name );
	    name = "reused-" + name;
	}
	// Bug -- what what about other attributes?
    }

    public String toString() {
	if (name != null) {
	    return "Intersection " + name;
	} else {
	    return "Intersection ???";
	}
    }
}

/** Main program
 *  @author Douglas Jones
 *  @version 2019-02-11
 *  @see Road
 *  @see Interseciton
 */
public class RoadNetwork {

    // lists of all the parts of this model
    private static List <Intersection> intersections
	= new LinkedList <Intersection> ();
    private static List <Road> roads
	= new LinkedList <Road> ();

    /** look up an intersection by name
     *  @param n the name of the intersection, possibly null (matches nothing)
     *  @returns the Intersection with that name, or null if no match
     */
    public static Intersection findIntersection( String n ) {
	// stupid code, a linear search, but does it matter?
	for (Intersection i: intersections) {
	    if (i.name.equals( n )) return i;
	}
	return null;
    }

    // build the road network by scanning a source file
    private static void buildNetwork( Scanner sc ) {
	// Bug -- what if there is no next
	while (sc.hasNext()) {
	    // pick off the next part of the network description
	    String command = sc.next();
	    if ("intersection".equals( command )) {
		intersections.add( new Intersection( sc ) );
	    } else if ("road".equals( command )) {
		roads.add( new Road( sc ) );
	    } else {
		Errors.warn( "invalid command " + command );
	    }
	    // Bug -- it would be nice to allow some kind of comments
	}
    }

    // print out the entire road network
    private static void printNetwork() {
	for (Intersection i: intersections) {
	    System.out.println( i );
	}
	for (Road r: roads) {
	    System.out.println( r );
	}
    }

    public static void main( String[] args ) {
	Errors.setPrefix( "RoadNetwork" );
	if (args.length < 1) {
	    Errors.warn( "missing argument" );
	    System.exit( 1 );
	}
	if (args.length > 1) {
	    Errors.warn( "extra arguments" );
	}
	try {
	    // args[0] is the text file holding the road network,
	    buildNetwork( new Scanner( new FileInputStream( args[0] ) ) ); 
	    printNetwork(); // something testable!
	} catch( FileNotFoundException e ) {
	    Errors.warn( "can't open file" );
	    System.exit( 1 );
	}
    }
}
