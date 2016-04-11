package org.agile.grenoble.twitter.filters;

import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.FilterFunction;

public class RemoveEmptyTweet implements FilterFunction<Tweet> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(Tweet value) {
        return  value != null ;
    }

}