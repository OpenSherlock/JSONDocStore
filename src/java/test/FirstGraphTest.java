/**
 * 
 */
package test;

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsEdge;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsGraphEnvironment;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsVertex;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 *
 */
public class FirstGraphTest {
	private JSONDocStoreEnvironment environment;
	private JSONDocStoreBlueprintsGraphEnvironment graphEnvironment;
	/**
	 * 
	 */
	public FirstGraphTest() {
		environment = new JSONDocStoreEnvironment();
		graphEnvironment = environment.getGraphEnvironment();
		System.out.println("FirstGraphTest 0");
		Graph g = graphEnvironment.getGraph();
		System.out.println("FirstGraphTest 1");
		Vertex v1 = g.addVertex("FirstVertex");
		System.out.println("FirstGraphTest 2 "+v1.getId());
		Vertex v2 = g.addVertex("SecondVertex");
		System.out.println("FirstGraphTest 3 "+v2.getId());
		Edge e1 = g.addEdge(null, v1, v2, "Happy Label");
		System.out.println("FirstGraphTest 4 "+e1.getLabel());
		JSONDocStoreBlueprintsVertex x = (JSONDocStoreBlueprintsVertex)g.getVertex("FirstVertex");
		System.out.println("AAA "+x.toJSONString());
		Iterable<Edge> y = g.getEdges();
		System.out.println("BBB "+y);
		x = (JSONDocStoreBlueprintsVertex)g.getVertex("SecondVertex");
		System.out.println("BBB "+x.toJSONString());
		environment.shutDown();
	}

}
