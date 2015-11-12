package org.agile.grenoble.twitter;

/**
 * Created by Laurent  on 12/11/15.
 */
public class SimpleTwitter {

    private String name ;
    private String tweet ;

    public SimpleTwitter(String n, String t) {
        name = n;
        tweet = t;
    }

    String getTwitterName() {
        return name;
    }

    String getTwittText() {
        return tweet;
    }
}