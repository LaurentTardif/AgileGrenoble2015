package org.agile.grenoble.twitter.filters;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.regex.Pattern;

/**
 * Created by adminpsl on 27/11/15.
 */
public class RemoveStopWord  implements FilterFunction<Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;
    public static final Pattern NOT_WORD_PATTERN = Pattern.compile("^[^\\w]*$");

    @Override
    public boolean filter(Tuple2<String, Integer> value) throws Exception {
        String word = value.f0;
        return (word != null && word.length()>4
                && !isUninitialized(word)
                && !isStopWord(word)
                && !isOnlySymbols(word))
                && !isTwittos(word);
    }

    private boolean isTwittos(String word) {
        return word.startsWith("@") || word.startsWith(".@") ;
    }

    //Grenoble
    //public boolean isAg15Tag(String value){return "#AG15".equalsIgnoreCase(value); }

    //Paris
    //public boolean isAg15Tag(String value){return "at2015".equalsIgnoreCase(value) ||"agiletour".equalsIgnoreCase(value) || "agiletourparis".equalsIgnoreCase(value); }

    //Toulouse
    public static boolean isStopWord(String value){return  "agiletourtoulouse".equalsIgnoreCase(value)
            || "agiletour".equalsIgnoreCase(value)
            || "agileparis".equalsIgnoreCase(value)
            || "agilegrenoble".equalsIgnoreCase(value)
            || "atnantes".equalsIgnoreCase(value)
            || "agile".equalsIgnoreCase(value)
            || "cest".equalsIgnoreCase(value)
            || "attls".equalsIgnoreCase(value); }
    public boolean isUninitialized(String value){
        return "uninitialized".equalsIgnoreCase(value);
    }

    public boolean isOnlySymbols(String value){
        return NOT_WORD_PATTERN.matcher(value).matches();
    }
}