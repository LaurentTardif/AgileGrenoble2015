package org.agile.grenoble.twitter.Mappers;



   /* example de tweet in json

        {"created_at":"Tue Nov 10 14:46:15 +0000 2015",
        "id":664091704534933504,"
        id_str":"664091704534933504",
     --->   "text":"RT @SofteamCadextan: Fin de journ\u00e9e avec une formation d'initiation \u00e0 l'Agile - #Agilit\u00e9 https:\/\/t.co\/2euIg5iS8H",
        "source":"\u003ca href=\"http:\/\/twitter.com\"
        rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","
        truncated":false,"in_reply_to_status_id":null,
        "in_reply_to_status_id_str":null,"in_reply_to_user_id":null,
        "in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,
        "user":{
            "id":216406867,"id_str":"216406867",
            "name":"Laurent Fourmy","screen_name":"LaurentFourmy",
            "location":"Sophia Antipolis","url":"http:\/\/www.softeam.fr","description":null,
            "protected":false,"verified":false,"followers_count":46,"friends_count":76,
            "listed_count":9,"favourites_count":4,"statuses_count":93,
            "created_at":"Tue Nov 16 16:55:03 +0000 2010","utc_offset":null,"time_zone":null,"geo_enabled":false,
            "lang":"fr","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED",
            "profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png",
            "profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png",
            "profile_background_tile":false,"profile_link_color":"0084B4","profile_sidebar_border_color":"C0DEED",
            "profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,
            "profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/491945647994462209\/rVdUOy_V_normal.jpeg",
            "profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/491945647994462209\/rVdUOy_V_normal.jpeg",
            "profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/216406867\/1445534192","default_profile":true,
            "default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},
          "geo":null,
          "coordinates":null,"place":null,"contributors":null,
          "retweeted_status":... }

         */

import org.agile.grenoble.twitter.streamData.Tweet;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
        This class is an helper to build a SimpleTwitter object from a json collected in realtime
        on the Twitter servers
     */
public class TweetFromJson implements MapFunction<String, Tweet> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TweetFromJson.class);

    @Override
    public Tweet map(String s) {

        String text=Tweet.Default_Text ,
               name =Tweet.Default_Name,
               geo = Tweet.Default_Geo,
               coordinate=Tweet.Default_Coordinate;

        try {
            JSONParser parser = new JSONParser(s);

            text = parser.parse("text").getString("retValue");
            name = parser.parse("user.name").getString("retValue");
            geo = parser.parse("geo.coordinates").getString("retValue");
            coordinate = parser.parse("user.location").getString("retValue");

        } catch (Exception e) {
            LOG.info("Fail to collect one of the data ");
            LOG.info("text =>" + text) ;
            LOG.info("name =>" + name) ;
            LOG.info("geo =>" + geo) ;
            LOG.info("coordinate =>" + coordinate) ;
        }

        Tweet st = new Tweet(name,text,geo,coordinate) ;
        LOG.info("Collect a twitt from " + st.getTwitterName());
        return st;
    }

}