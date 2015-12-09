package org.agile.grenoble.twitter.Mappers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.agile.grenoble.twitter.Mappers.TweetFromJson;
import org.agile.grenoble.twitter.Mappers.TweetFromTuple;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.junit.Test;

/**
 * Created by laurent.tardif on 12/9/2015.
 */
public class TestTweetFromJson extends TestCase {



    @Test
    public void test_ShouldResistToNull () {

        TweetFromJson mapper = new TweetFromJson();
        Tweet tweet = mapper.map(null) ;
        Assert.assertEquals(Tweet.Default_Name,tweet.getTwitterName());
        Assert.assertEquals(Tweet.Default_Text,tweet.getTwittText());
        Assert.assertEquals( Tweet.Default_Geo, tweet.getGeo());
        Assert.assertEquals( Tweet.Default_Coordinate,tweet.getCoordinate());
        tweet = mapper.map("") ;
        Assert.assertEquals(Tweet.Default_Name,tweet.getTwitterName());
        Assert.assertEquals(Tweet.Default_Text,tweet.getTwittText());
        Assert.assertEquals( Tweet.Default_Geo, tweet.getGeo());
        Assert.assertEquals( Tweet.Default_Coordinate,tweet.getCoordinate());
    }
}
