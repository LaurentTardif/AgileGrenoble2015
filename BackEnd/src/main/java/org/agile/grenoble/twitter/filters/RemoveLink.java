package org.agile.grenoble.twitter.filters;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class RemoveLink implements FilterFunction<Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean filter(Tuple2<String, Integer> value) throws Exception {
        return  (value.f0 != null && !value.f0.startsWith(("http")));
    }

}