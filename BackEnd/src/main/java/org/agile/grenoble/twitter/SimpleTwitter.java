package org.agile.grenoble.twitter;

/**
 * Created by Laurent  on 12/11/15.
 */
public class SimpleTwitter {

    private String name ;
    private String tweet ;
    private String geo ;
    private String coordinate ;

    public SimpleTwitter(String n, String t,String g, String c) {
        name = n;
        tweet = t;
        geo=g;
        coordinate = c;
    }

    String getGeo() {return geo;}
    String getCoordinate() {return coordinate;}

    String getTwitterName() {
        return name;
    }

    String getTwittText() {
        return tweet;
    }
}