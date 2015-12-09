package org.agile.grenoble.twitter.streamData;

/**
 * Created by Laurent  on 12/11/15.
 */
public class Tweet {

    public static final String Default_Name = "uninitialized name";
    public static final String Default_Text = "uninitialized tweet text";
    public static final String Default_Geo = "uninitialized geo";
    public static final String Default_Coordinate = "uninitialized coordinate";

    public static final Tweet Default_Tweet = new Tweet(Default_Name,Default_Text);

    private String name ;
    private String tweet ;
    private String geo ;
    private String coordinate ;

    public Tweet(String n, String t, String g, String c) {
        name = n == null ? Default_Name : n.trim();
        tweet = t == null ? Default_Text : t.trim();
        geo = g== null ? Default_Geo : g.trim();;
        coordinate = c == null ? Default_Coordinate : c.trim();;
    }

    public Tweet(String n, String t) {
      this(n,t,Default_Geo,Default_Coordinate);
    }

    public String getGeo() {return geo;}

    public String getCoordinate() {return coordinate;}

    public String getTwitterName() {
        return name;
    }

    public String getTwittText() {
        return tweet;
    }
}