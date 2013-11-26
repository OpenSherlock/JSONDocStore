/**
 * 
 */
package test;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
/**
 * @author park
 * Dump the KB and send it to a flat file
 */
public class DumpKBTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	private int counter = 0;
	private JSONParser parser = new JSONParser();
	private final String
		INDEX = BuildComplexKnowledgeBase.INDEX,
		TYPE = BuildComplexKnowledgeBase.TYPE,
		SUBOF =  BuildComplexKnowledgeBase.SUBOF,
		INSTANCEOF = BuildComplexKnowledgeBase.INSTANCEOF,
		LOCATOR = BuildComplexKnowledgeBase.LOCATOR;
	private Writer out;
	private List<String>seen = new ArrayList<String>();

	/**
	 * 
	 */
	public DumpKBTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		 File f = new File("KBDump"+System.currentTimeMillis()+".json");
		try {
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			out = new PrintWriter(bos);
			out.write("[");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		IResult rx = null;
		//Start with the root
		try {
			rx = model.getDocument(INDEX, TYPE, BuildComplexKnowledgeBase.ROOTID);
			displayErrorMessage(counter++,rx);
			JSONObject jo = (JSONObject)parser.parse((String)rx.getResultObject());
			//dump it
			out.write(jo.toJSONString());
			dumpTree(BuildComplexKnowledgeBase.ROOTID, rx);
			displayErrorMessage(counter++,rx);
			out.write("]");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		environment.shutDown();
	}
	
	void dumpTree(String parentId, IResult result) {
		System.out.println("Dumping "+result.hasError()+" "+parentId+" "+seen);
		//unwind if we have an error
		if (result.hasError())
			return;
		if (seen.contains(parentId))
			return;
		try {
			JSONObject jo;
			//fetch the document
//			result = model.getDocument(INDEX, TYPE, parentId);
//			displayErrorMessage(counter++,result);
//			if (result.hasError())
//				return;
//			String theNode = (String)result.getResultObject();
//			System.out.println("THENODE "+theNode);
//			jo = (JSONObject)parser.parse(theNode);
			//dump it
//			out.write(jo.toJSONString());
			//list Subs
			result = model.listDocumentsByProperty(INDEX, 
					SUBOF, parentId, 0, -1, TYPE);
			displayErrorMessage(counter++,result);
			if (result.hasError())
				return;
			List<String> kids= (List<String>)result.getResultObject();
			System.out.println("SUBS "+parentId+" "+kids);
			Iterator<String>itr = kids.iterator();
			String node, lox;
			if (kids != null && kids.size()>0) {
				itr = kids.iterator();
				while (itr.hasNext()) {
					node = itr.next();
					jo = (JSONObject)parser.parse(node);
					lox = (String)jo.get(LOCATOR);
					if (!seen.contains(lox)) {
						out.write(" , ");
						//dump it
						out.write(jo.toJSONString());
						//recurse
						lox = (String)jo.get(LOCATOR);
						jo = null;
						if (node != null)
							dumpTree(lox, result);
						seen.add(lox);
					}
				}
			}
			//list Instances
			result = model.listDocumentsByProperty(INDEX, 
					INSTANCEOF, parentId, 0, -1, TYPE);
			displayErrorMessage(counter++,result);
			if (result.hasError())
				return;
			kids = (List<String>)result.getResultObject();
			System.out.println("INSTANCES "+parentId+" "+kids);
			itr = kids.iterator();
			if (kids != null && kids.size()>0) {
				itr = kids.iterator();
				while (itr.hasNext()) {
					node = itr.next();
					jo = (JSONObject)parser.parse(node);
					lox = (String)jo.get(LOCATOR);
					if (!seen.contains(lox)) {
						out.write(" , ");
						//dump it
						out.write(jo.toJSONString());
						//we presume that instances have no children
						seen.add(lox);
					}
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
	}
	
	IResult listSubclasses(String parentId) {
		IResult result =  model.listDocumentsByProperty(INDEX, 
				SUBOF, 
				parentId, 0, -1, 
				TYPE);
		return result;
	}
	
	IResult listInstances(String parentId) {
		IResult result =  model.listDocumentsByProperty(INDEX, 
				INSTANCEOF, 
				parentId, 0, -1, 
				TYPE);
		return result;
	}
	
	
	void displayErrorMessage(int which, IResult rx) {
		System.out.println(which+" "+rx.hasError());
		if (rx.hasError())
			System.out.println(rx.getErrorString());
	}

}
