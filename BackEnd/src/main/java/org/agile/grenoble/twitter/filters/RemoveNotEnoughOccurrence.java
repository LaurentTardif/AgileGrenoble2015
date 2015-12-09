package org.agile.grenoble.twitter.Filters;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * Created by adminpsl on 30/11/15.
 */
public class RemoveNotEnoughOccurrence implements FilterFunction<Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;
    public static final int OccurrenceLimit = 2;

    @Override
    public boolean filter(Tuple2<String, Integer> value) {
        return (value.f1 > OccurrenceLimit) ;
    }

}