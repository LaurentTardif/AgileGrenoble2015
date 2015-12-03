package org.agile.grenoble.twitter.filters;

import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.FilterFunction;

public class RemoveEmptySimpleTwitter implements FilterFunction<Tweet> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(Tweet value) throws Exception {
        return  value != null ;
    }

}