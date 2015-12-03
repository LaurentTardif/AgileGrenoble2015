package org.agile.grenoble.twitter;

import junit.framework.TestCase;
import org.agile.grenoble.twitter.filters.RemoveStopWord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;



public class TestFilters extends TestCase{


    @Test
    public void testRemoveStopWord () throws  Exception{
        RemoveStopWord remover = new RemoveStopWord();
        assertFalse(remover.filter(new Tuple2<String, Integer>("attls", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("agiletour", 1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("atnantes", 1)));
        assertFalse(remover.filter(new Tuple2<String,Integer>("bon",1)));
        assertFalse(remover.filter(new Tuple2<String, Integer>("cest", 1)));
        assertTrue(remover.filter(new Tuple2<String, Integer>("genial", 1)));
        assertTrue(remover.filter(new Tuple2<String, Integer>("bonheur", 1)));

    }

}
