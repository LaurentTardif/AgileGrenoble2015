package org.agile.grenoble.twitter.Mappers;

import junit.framework.TestCase;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by adminpsl on 24/11/15.
 */
public class TestJsonParser extends TestCase {

    @Test
    public void testParsingOfHistoryFile () throws IOException, JSONException, URISyntaxException {

        File jsonFile = new File(TestJsonParser.class.getClassLoader().getSystemResource("TestJsonParser/history.json").toURI());
        Path jsonPath = jsonFile.toPath();
        List<String> jsonLines = Files.readAllLines(jsonPath, Charset.forName("UTF-8")) ;

        for (String jsonLine : jsonLines ) {
            JSONParser parser = new JSONParser(jsonLine);
            String author = parser.parse("user.name").getString("retValue");
            String text = parser.parse("text").getString("retValue");
            String geo = parser.parse("geo").getString("retValue");
            Assert.assertNotNull(author);
            Assert.assertEquals("Bad mapping of field during json parsing",author,"Laurent T");
            Assert.assertNotNull(text);
            Assert.assertNotNull(geo);
        }
    }
    @Test
    public void testParsingOfRealHistoryFile () throws IOException, JSONException,URISyntaxException {
        File jsonFile = new File(TestJsonParser.class.getClassLoader().getSystemResource("TestJsonParser/history.real.json").toURI());
        Path jsonPath = jsonFile.toPath();
        List<String> jsonLines = Files.readAllLines(jsonPath, Charset.forName("UTF-8")) ;
        int size = jsonLines.size() ;
        Assert.assertEquals("The size of the file read is not the good one", size, 193398);
        StringBuffer fullText = new StringBuffer();
        int current = 0 ;
        for (String jsonLine : jsonLines ) {
            fullText.append(jsonLine.trim());
        }
        //System.out.println("The big object is created") ;
        JSONObject obj = new JSONObject(fullText.toString());

        JSONArray arr = obj.getJSONArray("statuses");
        boolean foundOneAlfred = false ;
        boolean foundOneLaurent = false ;
        boolean foundOneAlexandre = false ;
        for (int i = 0; i < arr.length(); i++)
        {
            //System.out.println("i=" + i);
            String post = arr.getJSONObject(i).toString();
            Assert.assertNotNull("One of the post read is null, json parsing must have an issue", post);
            JSONParser parser = new JSONParser(post);
            String author = parser.parse("user.name").getString("retValue");
            String text = parser.parse("text").getString("retValue");
            String geo = parser.parse("geo").getString("retValue");
            Assert.assertNotNull(author);
            Assert.assertNotNull(text);
            Assert.assertNotNull(geo);


            //System.out.println("author [" + i + "] =>" + author);
            if (author.toLowerCase().contains("laurent"))  { foundOneLaurent=true ;}
            if (author.toLowerCase().contains("alfred"))  { foundOneAlfred= true;  }
            if (author.toLowerCase().contains("alexandre"))  {foundOneAlexandre= true; }
        }
        Assert.assertTrue("Crazy tweeters are not found" , foundOneLaurent && foundOneAlfred && foundOneAlexandre) ;

    }





}
