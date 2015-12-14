package org.agile.grenoble.twitter;

import junit.framework.Assert;
import org.agile.grenoble.twitter.Mappers.TestJsonParser;
import org.agile.grenoble.twitter.Mappers.TweetFromTuple;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Laurent on 03/12/15.
 */
public class TestSearchResultParser {


    private static final Logger LOG = LoggerFactory.getLogger(TestSearchResultParser.class);

    @Rule
    public ContiPerfRule i = new ContiPerfRule();


    @Test
    @PerfTest(invocations = 100, threads = 2)
    @Required(max = 2300, average = 1590)
    public void testParsingOfSearchResultFile () throws IOException, Exception,URISyntaxException {
        File jsonFile = new File(TestJsonParser.class.getClassLoader().getSystemResource("TestJsonParser/history.list").toURI());
        Path jsonPath = jsonFile.toPath();
        LOG.info("The file is going to be read") ;
        List<String> searchResultLines = Files.readAllLines(jsonPath, Charset.forName("UTF-8")) ;
        int size = searchResultLines.size() ;
        LOG.info("The file is read =>" +size +" line(s)") ;
        StringBuffer fullText = new StringBuffer();
        int current = 0 ;
        TweetFromTuple simpleConstructor = new TweetFromTuple();
        for (String searchResultLine : searchResultLines ) {
            Tweet currentTweet = simpleConstructor.map(searchResultLine);
            String post_id = currentTweet.getTwitterName();
            String post_text = currentTweet.getTwittText();
            String post_geo = currentTweet.getGeo() ;
            String post_coordinate = currentTweet.getCoordinate() ;
            //System.out.println("original post =>" + searchResultLine);
            //System.out.println("post_id =>" + post_id);
            Assert.assertNotNull(post_id);
            Assert.assertNotNull(post_text);
            Assert.assertNotNull(post_geo);
            Assert.assertNotNull(post_coordinate);
            Assert.assertEquals(post_geo, Tweet.Default_Geo);
            Assert.assertEquals(post_coordinate, Tweet.Default_Coordinate);
        }

    }

}
