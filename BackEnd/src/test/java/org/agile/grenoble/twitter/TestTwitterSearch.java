package org.agile.grenoble.twitter;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by adminpsl on 23/11/15.
 */
public class TestTwitterSearch extends TestCase {


    @Test
    public void testTwitterSearch() {
        String propertiesPath="/home/adminpsl/flinkDemo/twitter.properties";
        TwitterHistorySource twitterHistory = new TwitterHistorySource(propertiesPath);

    }

}