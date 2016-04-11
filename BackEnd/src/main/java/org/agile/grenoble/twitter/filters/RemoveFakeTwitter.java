package org.agile.grenoble.twitter.filters;

import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.apache.flink.api.common.functions.FilterFunction;

public class RemoveFakeTwitter  implements FilterFunction<NameAndCount> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(NameAndCount value)  {
        return (value!=null
                && value.f0 != null
                && ! value.f0.contains("Retweets")
                && ! value.f0.contains("France")
                && ! value.f0.contains("Music")) ;
    }

}