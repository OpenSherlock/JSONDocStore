/*
 * Copyright 2013, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package test;

import java.util.*;

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsGraphEnvironment;
import org.topicquests.persist.json.es.blueprints.api.IJSONGraph;

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
	private IJSONGraph graph;
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
	        Map<String,Object> m = new HashMap<String,Object>();
	        m.put("amount", 1.0);
	        m.put("date", 10);
	        aFriendB = graph.addEdge(null, a, b, "friend",m);
	        m.clear();
	        m.put("amount", 0.5);
	        aFriendC = graph.addEdge(null, a, c, "friend",m);
	        m.clear();
	        m.put("amount", 1.0);
	        aHateC = graph.addEdge(null, a, c, "hate",m);
	        m.clear();
	        m.put("amount", 1.0);
	        
	        cHateA = graph.addEdge(null, c, a, "hate",m);
	        m.clear();
	        m.put("amount", 0.4);
	        cHateB = graph.addEdge(null, c, b, "hate",m);
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
