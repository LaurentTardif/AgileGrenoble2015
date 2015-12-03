package org.agile.grenoble.twitter.filters;

import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.apache.flink.api.common.functions.FilterFunction;

public class RemoveEmptyNameAndCount implements FilterFunction<NameAndCount> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(NameAndCount value) throws Exception {
        return  (value.f0 != null && !value.f0.startsWith(("uninitialized")));
    }

}