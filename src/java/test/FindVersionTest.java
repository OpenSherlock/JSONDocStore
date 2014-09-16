/**
 * 
 */
package test;

import org.topicquests.persist.json.api.IJSONDocStoreOntology;

/**
 * @author park
 *
 */
public class FindVersionTest {

	/**
	 * 
	 */
	public FindVersionTest() {
		System.out.println("GOING");
		String testString = "{ \"foo\": \"bar\", \""+IJSONDocStoreOntology.VERSION_PROPERTY+"\": \"39787\", \"Ta\": \"ta\" }";
		System.out.println(testString);
		System.out.println(fetchVersionProperty(testString));
		System.exit(0);
	}
//////////////////////////
//	GOING
//	{ "foo": "bar", "_version": "39787", "Ta": "ta" }
//	39787
//////////////////////////
	String fetchVersionProperty(String jsonDoc) {
		String result = null;
		String x = jsonDoc;
		int where = x.indexOf(IJSONDocStoreOntology.VERSION_PROPERTY);
		if (where > 0) {
			//trim to the property field
			x = x.substring(where);
			where = x.indexOf(':');
			//skip past the ":"
			x = x.substring(where+1).trim();
			where = x.indexOf('"',1);
			//pluck the version number out of there
			result = x.substring(1, where);
		}
		return result;
	}

}
