package org.agile.grenoble.twitter.Mappers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.agile.grenoble.twitter.Mappers.TweetFromTuple;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.junit.Test;

/**
 * Created by laurent on 12/9/2015.
 */
public class TestTweetFromTuple extends TestCase{

    @Test
    public void test_ShouldReturnAValidTweet() throws Exception {
        String tweetAuthor = "laurent" ;
        String tweetText = "this is a simple tweet" ;
        String tweetSource = "@".concat(tweetAuthor).concat(":").concat(tweetText);
        TweetFromTuple mapper = new TweetFromTuple();
        Tweet tweet = mapper.map(tweetSource) ;
        Assert.assertEquals(tweetAuthor,tweet.getTwitterName());
        Assert.assertEquals(tweetText,tweet.getTwittText());
        Assert.assertEquals( Tweet.Default_Geo, tweet.getGeo());
        Assert.assertEquals( Tweet.Default_Coordinate,tweet.getCoordinate());
    }

    @Test
    public void test_ShouldReturnAValidReTweet() throws Exception {
        String tweetRealAuthor = "fred" ;
        String tweetAuthor = "@laurent - RT @" + tweetRealAuthor ;
        String tweetText = "this is a retweet" ;
        String tweetSource = tweetAuthor.concat(":").concat(tweetText);
        TweetFromTuple mapper = new TweetFromTuple();
        Tweet tweet = mapper.map(tweetSource) ;
        Assert.assertEquals(tweetRealAuthor,tweet.getTwitterName());
        Assert.assertEquals(tweetText,tweet.getTwittText());
        Assert.assertEquals( Tweet.Default_Geo, tweet.getGeo());
        Assert.assertEquals( Tweet.Default_Coordinate,tweet.getCoordinate());
    }

    @Test
    public void test_ShouldResistToNullOrEmpty() {
        TweetFromTuple mapper = new TweetFromTuple();
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
