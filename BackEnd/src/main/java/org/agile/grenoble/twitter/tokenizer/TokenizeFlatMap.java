package org.agile.grenoble.twitter.tokenizer;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.StringTokenizer;

/**
 * Makes sentences from English tweets.
 * <p>
 * Implements a string tokenizer that splits sentences into words as a
 * user-defined FlatMapFunction. The function takes a line (String) and
 * splits it into multiple pairs in the form of "(word,1)" ({@code Tuple2<String,
 * Integer>}).
 */
public class TokenizeFlatMap extends RichFlatMapFunction<String, Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(value);

        // split the message
        while (tokenizer.hasMoreTokens()) {
            String result = tokenizer.nextToken().replaceAll("\\s*", "").toLowerCase();

            if (result != null && !result.equals("")) {
                out.collect(new Tuple2<String, Integer>(result.trim().replace(",", "")
                        .replace("#", "")
                        .replace(".", "")
                        .replace("?", "")
                        .replace("!", "")
                        .replace("'", "")
                        .replace("&amp;", "")
                        .replace("\"", ""), 1));
            }
        }
    }

}