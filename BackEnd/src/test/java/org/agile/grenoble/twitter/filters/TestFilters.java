package org.agile.grenoble.twitter.filters;

import junit.framework.TestCase;
import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;



public class TestFilters extends TestCase {

    @Test
    public void testRemoveLinks () throws  Exception{
        RemoveLink remover = new RemoveLink();

        //check if http and https are removed
        assertFalse(remover.filter(new Tuple2<String, Integer>("http://foo.com/", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("https://foo.com", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("http://foo.com:8080/", 1)));

        //check if some normal case works
        assertTrue(remover.filter(new Tuple2<String, Integer>("genial", 1)));
        assertTrue(remover.filter(new Tuple2<String, Integer>("bonheur", 1)));
    }

    @Test
    public void testRemoveStopWord () throws  Exception{
        RemoveStopWord remover = new RemoveStopWord();

        //should resist to null and empty
        assertFalse(remover.filter(new Tuple2<String, Integer>("", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>(null, 1)));
        //check if agile tour name are removed
        assertFalse(remover.filter(new Tuple2<String, Integer>("attls", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("agiletour", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("atnantes", 1)));
        assertFalse(remover.filter(new Tuple2<String,Integer>("bon",1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("cest", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("test", 1)));

        //check if some normal case works
        assertTrue(remover.filter(new Tuple2<String, Integer>("genie", 1)));
        assertTrue(remover.filter(new Tuple2<String, Integer>("bonheur", 1)));
    }

    @Test
    public void testRemoveEmptyTweet() {
        RemoveEmptyTweet remover = new RemoveEmptyTweet();
        //should resist to null and empty
        assertFalse(remover.filter(null));
        assertTrue(remover.filter(Tweet.Default_Tweet));

    }

    @Test
    public void testRemoveEmptyNameAndCount() {
        RemoveEmptyNameAndCount remover = new RemoveEmptyNameAndCount();
        //should resist to null and empty
        assertFalse(remover.filter(null));
        assertTrue(remover.filter(new NameAndCount("foo",1)));


    }
    @Test
    public void testRemoveFakeTweeter() {
        RemoveFakeTwitter remover = new RemoveFakeTwitter();
        //should resist to null and empty
        assertFalse(remover.filter(null));
        assertFalse(remover.filter(new NameAndCount("France",1)));
        assertFalse(remover.filter(new NameAndCount("Music",1)));
        assertFalse(remover.filter(new NameAndCount("Retweets yeah",1)));
        assertTrue(remover.filter(new NameAndCount("foo",1)));


    }

    @Test
    public void testRemoveNotEnoughOccurence() {
        RemoveNotEnoughOccurrence remover = new RemoveNotEnoughOccurrence();
        //should resist to null and empty
        assertFalse(remover.filter(new Tuple2<String, Integer>("", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>(null, 1)));
        //should remove occurence < RemoveNotEnoughOccurrence.OccurenceLimit
        assertFalse(remover.filter(new Tuple2<String, Integer>("foo", RemoveNotEnoughOccurrence.OccurrenceLimit)));
        assertFalse(remover.filter(new Tuple2<String, Integer>(null, RemoveNotEnoughOccurrence.OccurrenceLimit-1)));

        //Should not remove this one
        assertTrue(remover.filter(new Tuple2<String, Integer>(null, RemoveNotEnoughOccurrence.OccurrenceLimit+1)));
    }

}
