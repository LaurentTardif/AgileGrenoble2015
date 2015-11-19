package org.agile.grenoble.twitter;

import org.apache.flink.api.java.tuple.Tuple2;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;

/**
 * Created by adminpsl on 19/11/15.
 */
public class NameAndCount extends Tuple2<String, Integer> {

    public NameAndCount() {

    }
    public NameAndCount(Tuple2<String, Integer> value) {
      this(value.f0,value.f1);
    }

    public NameAndCount(String name, int value) {
        f0 = escapeJavaScript(name.replace("\"","").replace("'","")) ;
        f1 = value ;
    }

    public String toString() {
        return "[\""+f0 +"\"," + f1 +"]" ;
    }


}
