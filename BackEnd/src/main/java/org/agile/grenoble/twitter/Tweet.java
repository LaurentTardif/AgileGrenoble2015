package org.agile.grenoble.twitter;

/**
 * Created by Laurent  on 12/11/15.
 */
public class Tweet {

    private String name ;
    private String tweet ;
    private String geo ;
    private String coordinate ;

    public Tweet(String n, String t, String g, String c) {
        name = n == null ? null : n.trim();
        tweet = t == null ? null : t.trim();
        geo=g;
        coordinate = c;
    }
    public Tweet(String n, String t) {
        name = n == null ? null : n.trim();
        tweet = t == null ? null : t.trim();
        geo="On the moon";
        coordinate = "Moon coordinate";
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