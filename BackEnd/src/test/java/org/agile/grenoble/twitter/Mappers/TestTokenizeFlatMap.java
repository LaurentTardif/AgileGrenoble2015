package org.agile.grenoble.twitter.Mappers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void test_NativeBehavior() {
        //flatMap(String value, Collector<Tuple2<String, Integer>> out
        TokenizeFlatMap tokenizer = new TokenizeFlatMap();
        SimpleCollector out = new SimpleCollector();
        tokenizer.flatMap("this is a nice String",out);
        Assert.assertTrue(out.contains("this"));
        Assert.assertTrue(out.contains("is"));
        Assert.assertTrue(out.contains("a"));
        Assert.assertTrue(out.contains("nice"));
        Assert.assertTrue(out.contains("string"));
    }
}
