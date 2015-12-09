package org.agile.grenoble.twitter.Filters;

import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.FilterFunction;

public class RemoveEmptyNameAndCount implements FilterFunction<NameAndCount> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(NameAndCount value)  {
        return  (  value != null && value.f0 != null
                && !
                (    value.f0.startsWith(Tweet.Default_Name)
                  || value.f0.startsWith(Tweet.Default_Text) )
        );
    }

}
