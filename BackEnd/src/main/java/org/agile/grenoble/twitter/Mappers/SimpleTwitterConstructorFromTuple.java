package org.agile.grenoble.twitter.Mappers;

import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
          This class is an helper to build a SimpleTwitter object from a json collected in realtime
          on the Twitter servers
       */
public  class SimpleTwitterConstructorFromTuple implements MapFunction<String, Tweet> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTwitterConstructorFromTuple.class);

    @Override
    public Tweet map(String s)
            throws Exception {
        String text="uninitialized text" ,
                name ="uninitialized name" ;

        if ( s != null && ! s.isEmpty())  {
            int ind = s.indexOf(':');
            if (ind == -1) {
                LOG.info("====> invalid static twitt from " + s);
            } else {
                name = s.substring(0, ind);
                text = s.substring(ind);
            }
        }
        Tweet st = new Tweet(name,text) ;
        LOG.info("Collect a static twitt from " + st.getTwitterName());
        return st;
    }

}