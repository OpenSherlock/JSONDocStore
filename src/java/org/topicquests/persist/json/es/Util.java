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
package org.topicquests.persist.json.es;

import java.io.IOException;
import java.util.UUID;

import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.simple.JSONObject;

/**
 * @author park
 * Just collecting stuff for ideas; may never use
 */
public class Util {

	/**
	 * 
	 */
	public Util() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
{
    "tweet" : {
        "properties" : {
            "message" : {"type" : "string", "store" : "yes"}
        }
    }
}

	 * @param type
	 * @param field
	 * @param fieldType
	 * @param stored
	 * @return
	 */
	public String createMapString(String type, String field, String fieldType, boolean stored) {
		JSONObject jo = new JSONObject();
		JSONObject j = new JSONObject();
		JSONObject ij = new JSONObject();
		JSONObject iij = new JSONObject();
		iij.put("type", fieldType);
		String isstore = "yes";
		if (!stored)
			isstore = "no";
		iij.put("store", isstore);
		ij.put("message", iij);
		j.put("properties",ij);
		jo.put(type, j);
		return jo.toJSONString();
	}
	
	/**
	 * https://github.com/lukas-vlcek/elasticsearch.demo/blob/master/src/test/java/org/elasticsearch/demo/BaseTestSupport.java
	 * @param room
	 * @param color
	 * @return
	 * @throws IOException
	 */
	 byte[] createSource(String room, String color) throws IOException {
	        return XContentFactory.jsonBuilder()
	                .startObject()
	                .field("room", room)
	                .field("color", color)
	                .endObject()
	                .bytes().toBytes();
	    }

	 /**
        client.prepareIndex("house", "room", "1").setSource(createSource("livingroom", "red")).execute().actionGet();
        client.prepareIndex("house", "room", "2").setSource(createSource("familyroom", "white")).execute().actionGet();
        client.prepareIndex("house", "room", "3").setSource(createSource("kitchen", "blue")).execute().actionGet();
        client.prepareIndex("house", "room", "4").setSource(createSource("bathroom", "white")).execute().actionGet();
        client.prepareIndex("house", "room", "5").setSource(createSource("garage", "blue")).execute().actionGet();
	
       // Refresh index reader
        client.admin().indices().refresh(Requests.refreshRequest("_all")).actionGet();

        // Prepare and execute query
        QueryBuilder queryBuilder = QueryBuilders.termQuery("color", "white");

        SearchResponse resp = client.prepareSearch("house")
                .setTypes("room")
// .setSearchType(SearchType.DEFAULT)
                .setQuery(queryBuilder)
// .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        // Make sure we got back expected data
        List<String> expected = new ArrayList<String>();
        expected.add("2");
        expected.add("4");

        assertEquals(expected.size(), resp.getHits().getTotalHits());

        for (SearchHit hit : resp.getHits().getHits()) {
            assertTrue(expected.contains(hit.id()));
        }
   	  */
}
