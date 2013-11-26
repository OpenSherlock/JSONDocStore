/**
 * 
 */
package test;

import java.util.Collection;
import java.util.Iterator;

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsGraphEnvironment;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 *
 */
public class GraphTestSuite {
	private JSONDocStoreEnvironment environment;
	private JSONDocStoreBlueprintsGraphEnvironment graphEnvironment;
	private Graph graph;
    Vertex a;
    Vertex b;
    Vertex c;
    Edge aFriendB;
    Edge aFriendC;
    Edge aHateC;
    Edge cHateA;
    Edge cHateB;

	/**
	 * 
	 */
	public GraphTestSuite() {
		environment = new JSONDocStoreEnvironment();
		graphEnvironment = environment.getGraphEnvironment();
		System.out.println("GraphTestSuite 0");
		graph = graphEnvironment.getGraph();
	       a = graph.addVertex(null);
	        b = graph.addVertex(null);
	        c = graph.addVertex(null);
	        aFriendB = graph.addEdge(null, a, b, "friend");
	        aFriendC = graph.addEdge(null, a, c, "friend");
	        aHateC = graph.addEdge(null, a, c, "hate");
	        cHateA = graph.addEdge(null, c, a, "hate");
	        cHateB = graph.addEdge(null, c, b, "hate");
	        aFriendB.setProperty("amount", 1.0);
	        aFriendB.setProperty("date", 10);
	        aFriendC.setProperty("amount", 0.5);
	        aHateC.setProperty("amount", 1.0);
	        cHateA.setProperty("amount", 1.0);
	        cHateB.setProperty("amount", 0.4);
			System.out.println("GraphTestSuite-1 "+a.toString());
			System.out.println("GraphTestSuite-1.1 "+aFriendB.toString());
			System.out.println("GraphTestSuite-1.2 "+aFriendC.toString());
	        //now the query
	        Object o = a.query().labels("friend").hasNot("date").edges();
			System.out.println("GraphTestSuite-2 "+o);
     
	        Iterable x = (Iterable)o;
			System.out.println("GraphTestSuite-3 "+x);
	        Iterator itr = x.iterator();
			System.out.println("GraphTestSuite-4 "+itr.hasNext());
	        int count = 0;
	        while (itr.hasNext()) {
	        	System.out.println(itr.next());
	        	count++;
	        }
	        
            System.out.println("AAA "+count+" | "+1);
            o = a.query().labels("friend");
            System.out.println("MMMM "+o);
            System.out.println("BBB "+a.query().labels("friend").hasNot("date").edges().iterator().next().getProperty("amount")+" | "+ 0.5);

		
		environment.shutDown();
	}

}
