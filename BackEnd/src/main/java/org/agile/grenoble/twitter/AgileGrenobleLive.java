/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.agile.grenoble.twitter;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * This is an example how to use TwitterFilterSource. Before executing the
 * example you have to define the access keys of twitter.properties in the
 * resource folder. The access keys can be found in your twitter account.
 */
public class AgileGrenobleLive {



    private static final Logger LOG = LoggerFactory.getLogger(AgileGrenobleLive.class);
	/**
	 * path to the twitter properties
	 */


	public static void main(String[] args) {
		if (!parseParameters(args)) {
			System.out.println("Arguments fail!");
			System.exit (1);
		};

		final StreamExecutionEnvironment env = StreamExecutionEnvironment
				.getExecutionEnvironment();


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





		/*TwitterFilterSource twitterSource = new TwitterFilterSource(
				TwitterFilterSourceExample.class.getResource(propertiesPath)
						.getFile());
		*/
		TwitterFilterSource twitterSource = new TwitterFilterSource(propertiesPath);
        //we can add several track term
        //twitterSource.trackTerm("#agile");
        twitterSource.trackTerm("#AgileGrenoble");
        twitterSource.trackTerm("#agileGrenoble2015");
        twitterSource.trackTerm("#agilegrenoble");
        twitterSource.trackTerm("#agilegrenoble2015");
        twitterSource.trackTerm("#ag2015");
        twitterSource.trackTerm("#ag15");
        twitterSource.trackTerm("#Ag15");
        twitterSource.trackTerm("#AG15");
        //twitterSource.trackTerm("grenoble");

        //define the language of the twitt
        twitterSource.filterLanguage("fr");
        twitterSource.filterLanguage("en");


        DataStream<String> staticjson = env.readTextFile("/home/adminpsl/flinkDemo/twits.txt");

        //build the twitt stream (it will be in json) then mapped to a stream of simpleTwitter object
		DataStream<String> dynamicjson = env.addSource(twitterSource);

        DataStream<String> json = dynamicjson.union(staticjson);

        DataStream<SimpleTwitter> streamSource = json.map(new SimpleTwitterConstructor());

        DataStream<NameAndCount> streamTwittos = streamSource
                .filter(new RemoveEmptySimpleTwitter())
                .map(new MapFunction<SimpleTwitter, NameAndCount>() {
                    @Override
                    public NameAndCount map(SimpleTwitter simpleTwitter) throws Exception {
                        return new NameAndCount(simpleTwitter.getTwitterName(), 1);
                    }
                })
                .filter(new RemoveEmpty())
                .filter(new RemoveFakeTwitter())
                    // group by words and sum their occurrences
                .keyBy(0).sum(1);

        DataStream<NameAndCount> streamTwits = streamSource
                .map(new MapFunction<SimpleTwitter, String>() {
                    @Override
                    public String map(SimpleTwitter simpleTwitter) throws Exception {
                        return simpleTwitter.getTwittText();
                    }
                })
                .flatMap(new TokenizeFlatMap())
                .filter(new RemoveLink())
                .filter(new RemoveStopWord())
                .keyBy(0).sum(1)
                .map(new MapFunction<Tuple2<String, Integer>, NameAndCount>() {
                    @Override
                    public NameAndCount map(Tuple2<String, Integer> value) throws Exception {
                        return new NameAndCount(value);
                    }
                });
                // group by words and sum their occurrences;
        /*
        DataStream<Tuple2<String, Integer>> streamGeo = streamSource
                .map(new MapFunction<SimpleTwitter, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(SimpleTwitter simpleTwitter) throws Exception {
                        return new Tuple2<String, Integer>(simpleTwitter.getGeo(),1);
                    }
                })
                // group by words and sum their occurrences
                .keyBy(0).sum(1);

        DataStream<Tuple2<String, Integer>> streamCoordinate = streamSource
                .map(new MapFunction<SimpleTwitter, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(SimpleTwitter simpleTwitter) throws Exception {
                        return new Tuple2<String, Integer>(simpleTwitter.getCoordinate(),1);
                    }
                })
                // group by words and sum their occurrences
                .keyBy(0).sum(1);

        */

        json.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        //streamSource.print();
        streamTwittos.writeAsText(outputPath + ".twittos", FileSystem.WriteMode.OVERWRITE);
        streamTwits.writeAsText(outputPath+".twits", FileSystem.WriteMode.OVERWRITE);
        //streamGeo.writeAsText(outputPath+".geo", FileSystem.WriteMode.OVERWRITE);
        //streamCoordinate.writeAsText(outputPath+".coordinate", FileSystem.WriteMode.OVERWRITE);

		try {
            if (LOG.isInfoEnabled()) {
                LOG.info("Twitter Streaming API tracking AGILE in progress");
            }
			env.execute("Twitter Streaming Test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean fileInput = false;
	private static boolean fileOutput = false;
	private static String propertiesPath;
	private static String outputPath;

	private static boolean parseParameters(String[] args) {
		if (args.length > 0) {
			// parse input arguments
			fileOutput = true;
			if (args.length == 2) {
				fileInput = true;
				propertiesPath = args[0];
				outputPath = args[1];
			} else if (args.length == 1) {
				outputPath = args[0];
			} else {
				System.err.println("USAGE:\nTwitterStream [<pathToPropertiesFile>] <result path>");
				return false;
			}
		} else {
			System.out.println("Executing TwitterStream example with built-in default data.");
			System.out.println("  Provide parameters to read input data from a file.");
			System.out.println("  USAGE: TwitterStream [<pathToPropertiesFile>] <result path>");
		}
		return true;
	}

    // *************************************************************************
    // USER FUNCTIONS
    // *************************************************************************

    /**
     * Makes sentences from English tweets.
     * <p>
     * Implements a string tokenizer that splits sentences into words as a
     * user-defined FlatMapFunction. The function takes a line (String) and
     * splits it into multiple pairs in the form of "(word,1)" ({@code Tuple2<String,
     * Integer>}).
     */
    public static class TokenizeFlatMap extends RichFlatMapFunction<String, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            StringTokenizer tokenizer = new StringTokenizer(value);

            // split the message
            while (tokenizer.hasMoreTokens()) {
                String result = tokenizer.nextToken().replaceAll("\\s*", "").toLowerCase();

                if (result != null && !result.equals("")) {
                    out.collect(new Tuple2<String, Integer>(result, 1));
                }
            }
        }

    }
    public static class RemoveEmptySimpleTwitter implements FilterFunction<SimpleTwitter> {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean filter(SimpleTwitter value) throws Exception {
            return  value != null ;
        }

    }

    public static class RemoveEmpty implements FilterFunction<NameAndCount> {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean filter(NameAndCount value) throws Exception {
            return  (value.f0 != null && !value.f0.startsWith(("uninitialized")));
        }

    }
    public static class RemoveLink implements FilterFunction<Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean filter(Tuple2<String, Integer> value) throws Exception {
            return  (value.f0 != null && !value.f0.startsWith(("http")));
        }

    }
    public static class RemoveFakeTwitter  implements FilterFunction<NameAndCount> {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean filter(NameAndCount value) throws Exception {
            return (value.f0 != null
                    && ! value.f0.contains("Retweets")
                    && ! value.f0.contains("France")
                    && ! value.f0.contains("Music")) ;
        }

    }

    public static class RemoveStopWord  implements FilterFunction<Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;
        public static final Pattern NOT_WORD_PATTERN = Pattern.compile("^[^\\w]*$");

        @Override
        public boolean filter(Tuple2<String, Integer> value) throws Exception {
            String word = value.f0;
            return (word != null && word.length()>4 && !isUninitialized(word) && !isAg15Tag(word) && !isOnlySymbols(word)) ;
        }
         public boolean isAg15Tag(String value){
           return "#AG15".equalsIgnoreCase(value);
        }
        public boolean isUninitialized(String value){
            return "uninitialized".equalsIgnoreCase(value);
        }

        public boolean isOnlySymbols(String value){
            return NOT_WORD_PATTERN.matcher(value).matches();
        }
    }
    public static class RemoveNotEnoughOccurence  implements FilterFunction<Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean filter(Tuple2<String, Integer> value) throws Exception {
            return (value.f1 > 2) ;
        }

    }

    public static class SimpleTwitterConstructor implements MapFunction<String, SimpleTwitter> {
        private static final long serialVersionUID = 1L;

        @Override
        public SimpleTwitter map(String s)
        throws Exception {
            String text="uninitialized text" ,
                    name ="uninitialized name",
                    geo = "uninitialized geo",
                    coordinate="uninitialized coordinate";

            try {
                text = JSONParseFlatMap.getString(s, "text");
            } catch (Exception e) {
                System.err.println("Fail to collect text")	;
            }
            try {
                name = JSONParseFlatMap.getString(s, "user.name");
            } catch (Exception e) {
                System.err.println("Fail to collect name")	;
            }
            try {
                geo = JSONParseFlatMap.getString(s, "geo.coordinates");
            } catch (Exception e) {
                System.err.println("Fail to collect geo")	;
            }
            try {
                coordinate = JSONParseFlatMap.getString(s, "user.location");
            } catch (Exception e) {
                System.err.println("Fail to collect coordinates")	;
            }

            SimpleTwitter st = new SimpleTwitter(name,text,geo,coordinate) ;
            LOG.info("Collect a twitt from " + st.getTwitterName());
            return st;
            //c.collect(s);
        }

    }


}
