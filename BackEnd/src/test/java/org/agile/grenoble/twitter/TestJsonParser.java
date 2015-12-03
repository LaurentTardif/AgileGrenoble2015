package org.agile.grenoble.twitter;

import junit.framework.TestCase;
import org.agile.grenoble.twitter.Mappers.JSONParser;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by adminpsl on 24/11/15.
 */
public class TestJsonParser extends TestCase {


    @Test
    public void testParsingOfHistoryFile () throws IOException, JSONException, URISyntaxException {
        assertTrue("Basic test for framework validation", true);

        //URL url = getClass().getResource("TestJsonParser/history.json");
        //File jsonFile = new File(url.toURI());
        File jsonFile = new File("/home/adminpsl/AgileGrenoble2015/BackEnd/src/test/resources/TestJsonParser/history.json");
        Path jsonPath = jsonFile.toPath();
        List<String> jsonLines = Files.readAllLines(jsonPath) ;

        for (String jsonLine : jsonLines ) {
            assertTrue("Basic test for framework validation" ,true);
            //System.out.println("a line is read " + jsonLine );
            JSONParser parser = new JSONParser(jsonLine);
            String author = parser.parse("user.name").getString("retValue");
            String text = parser.parse("text").getString("retValue");
            String geo = parser.parse("geo").getString("retValue");
            assertTrue("Basic test for framework validation" + author ,true);
            System.out.println("The author found is  " + author);
            System.out.println("The text found is  " + text);
        }
    }
    @Test
    public void testParsingOfSearchFile () throws IOException, JSONException {
        assertTrue("Basic test for framework validation", true);
        File jsonFile = new File("/home/adminpsl/AgileGrenoble2015/BackEnd/src/test/resources/TestJsonParser/history.json.v0");
        Path jsonPath = jsonFile.toPath();
        System.out.println("The file is going to be read") ;
        List<String> jsonLines = Files.readAllLines(jsonPath) ;
        int size = jsonLines.size() ;
        System.out.println("The file is read =>" +size +" line(s)") ;
        StringBuffer fullText = new StringBuffer();
        int current = 0 ;
        for (String jsonLine : jsonLines ) {
            //System.out.println(current +"/"+ size);current++;
            fullText.append(jsonLine.trim());
        }
        System.out.println("The big object is created") ;
        JSONObject obj = new JSONObject(fullText.toString());
        //String pageName = obj.getJSONObject("statuses").getString("pageName");

        JSONArray arr = obj.getJSONArray("statuses");
        System.out.println("We have " + arr.length() + " status") ;
        for (int i = 0; i < arr.length(); i++)
        {
            String post_id = arr.getJSONObject(i).getString("text");
            System.out.println("post_id ["+i+"] =>" + post_id);
        }

    }


}
