package org.agile.grenoble.twitter.Mappers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by laurent.tardif on 12/9/2015.
 */
public class TestTokenizeFlatMap extends TestCase {

    class SimpleCollector extends ArrayList<String> implements Collector<Tuple2<String, Integer>> {

        @Override
        public void collect(Tuple2<String, Integer> value) {
            add(value.f0);
        }

        @Override
        public void close() {
            //Nothing to do for list
        }
    }

    @Test
    public void test_Native_Behavior() {
        TokenizeFlatMap tokenizer = new TokenizeFlatMap();
        SimpleCollector out = new SimpleCollector();
        tokenizer.flatMap("this is a nice String",out);
        assertTrue(out.contains("this"));
        assertTrue(out.contains("is"));
        assertTrue(out.contains("a"));
        assertTrue(out.contains("nice"));
        assertTrue(out.contains("string"));
        tokenizer.flatMap("this is another nice String",out);
        assertEquals(10,out.size());
    }

    @Test
    public void test_Should_Resist_To_Null_And_Empty() {
        TokenizeFlatMap tokenizer = new TokenizeFlatMap();
        SimpleCollector out = new SimpleCollector();
        tokenizer.flatMap("",out);
        assertTrue(out.isEmpty());
        tokenizer.flatMap(null,out);
        assertTrue(out.isEmpty());

    }


}
