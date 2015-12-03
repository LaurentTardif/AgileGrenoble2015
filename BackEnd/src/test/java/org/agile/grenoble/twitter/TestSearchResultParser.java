package org.agile.grenoble.twitter;

import junit.framework.TestCase;
import org.agile.grenoble.twitter.Mappers.SimpleTwitterConstructorFromTuple;
import org.agile.grenoble.twitter.streamData.Tweet;
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
 * Created by adminpsl on 03/12/15.
 */
public class TestSearchResultParser extends TestCase{

    @Test
    public void testParsingOfSearchResultFile () throws IOException, Exception,URISyntaxException {
        File jsonFile = new File(TestJsonParser.class.getClassLoader().getSystemResource("TestJsonParser/history.list").toURI());
        Path jsonPath = jsonFile.toPath();
        System.out.println("The file is going to be read") ;
        List<String> searchResultLines = Files.readAllLines(jsonPath) ;
        int size = searchResultLines.size() ;
        System.out.println("The file is read =>" +size +" line(s)") ;
        StringBuffer fullText = new StringBuffer();
        int current = 0 ;
        SimpleTwitterConstructorFromTuple simpleConstructor = new SimpleTwitterConstructorFromTuple();
        for (String searchResultLine : searchResultLines ) {
            Tweet currentTweet = simpleConstructor.map(searchResultLine);
            String post_id = currentTweet.getTwitterName();
            String post_text = currentTweet.getTwittText();
            System.out.println("post_id =>" + post_id);
        }

    }

}
