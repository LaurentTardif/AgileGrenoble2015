package org.agile.grenoble.twitter.Mappers;

import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
          This class is an helper to build a SimpleTwitter object from a json collected in realtime
          on the Twitter servers
       */
public  class TweetFromTuple implements MapFunction<String, Tweet> {
    private static final String RT = "- RT @";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TweetFromTuple.class);

    @Override
    public Tweet map(String s) {
        String text="uninitialized text" ,
                name ="uninitialized name" ;

        if ( s == null ||  s.isEmpty() ) {
            LOG.info("The string is empty, return a default tweet");
            return Tweet.Default_Tweet;
        }

        int ind = s.indexOf(':');
        if (ind == -1) {
            LOG.info("We have not found author/text separator in the tweet (:) " + s );
            return Tweet.Default_Tweet;
        }
        name = s.substring(1, ind);
        if (name.contains(RT)) {
            //it is a retweet
            LOG.info("it is a re tweet");
            int ind1 = name.indexOf(RT)+RT.length();
            name = name.substring(ind1);

        }
        text = s.substring(ind+1);
        Tweet st = new Tweet(name, text);
        LOG.info("Mapped a tweet from " + st.getTwitterName());
        return st;
    }

}