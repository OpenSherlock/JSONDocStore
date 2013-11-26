/**
 * 
 */
package test;

import org.topicquests.util.LoggingPlatform;

/**
 * @author park
 * 
 */
public class TestHarness {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoggingPlatform logger = LoggingPlatform.getInstance("logger.properties");
		System.out.println("Starting");
		//new FirstTest();
		//new SecondTest();
		//new ThirdTest();
		//new RemoveTest();
		//new FirstBigQueryTest();
		//new SecondBigQueryTest();
		//new FirstCountTest();
		new BuildComplexKnowledgeBase();
		new FirstKBTest();
		//new DumpKBTest();
		//new TreeTest();
		//new ExtraKBTest();
		//new FirstGraphTest();
		new GraphTestSuite();
		System.out.println("Did");
	}

}
