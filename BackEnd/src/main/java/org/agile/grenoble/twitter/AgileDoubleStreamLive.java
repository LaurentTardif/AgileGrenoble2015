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

import org.agile.grenoble.twitter.Mappers.TweetFromJson;
import org.agile.grenoble.twitter.Mappers.TweetFromTuple;
import org.agile.grenoble.twitter.Filters.*;
import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.agile.grenoble.twitter.Mappers.TokenizeFlatMap;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * This is an example how to use TwitterFilterSource. Before executing the
 * example you have to define the access keys of twitter.properties in the
 * resource folder. The access keys can be found in your twitter account.
 */
public class AgileDoubleStreamLive {


    private static boolean fileInput = false;
    private static boolean fileOutput = false;
    private static String propertiesPath;
    private static String outputPath;
    private static final Logger LOG = LoggerFactory.getLogger(AgileDoubleStreamLive.class);
	/**
	 * path to the twitter properties
	 */


	public static void main(String[] args) {
		if (!parseParameters(args)) {
			System.out.println("Arguments fail!");
			System.exit (1);
		};


        final String historyTupleFilePath = "/home/adminpsl/flinkDemo/historyTuple.txt";
        final String historyLiveJsonFilePath = "/home/adminpsl/flinkDemo/historyLive.json";
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final Collection<String> firstTrackedTermsCollection =  Arrays.asList("#attls", "#agileToulouse") ;
        final Collection<String> firstTrackedLaguageCollection =  Arrays.asList("fr") ;
        final String dedicatedFirstStreamOutputFileName = "attls_fr.json" ;


        final Collection<String> secondTrackedTermsCollection =  Arrays.asList("#grenoble", "#agilegrenoble2015") ;
        final Collection<String> secondTrackedLaguageCollection =  Arrays.asList("en") ;
        final String dedicatedSecondStreamOutputFileName = "attls_en.json" ;

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);






		TwitterFilterSource twitterFirstStream = new TwitterFilterSource(propertiesPath);
        //we can add several track term
        twitterFirstStream.trackTerms(firstTrackedTermsCollection);

        //define the language of the twitt
        twitterFirstStream.filterLanguages(firstTrackedLaguageCollection);

        TwitterFilterSource twitterSecondStream = new TwitterFilterSource(propertiesPath);
        //we can add several track term
        twitterSecondStream.trackTerms(secondTrackedTermsCollection);


        //define the language of the twitt
        twitterSecondStream.filterLanguages(secondTrackedLaguageCollection);



        DataStream<String> ListHistoryTuple = env.readTextFile(historyTupleFilePath);
        DataStream<Tweet> FlowHistoryTweets = ListHistoryTuple.map(new TweetFromTuple());


        //Nantes
        DataStream<String> RealTimeSecondStreamTweets = env.addSource(twitterSecondStream);


        //build the twitt stream (it will be in json) then mapped to a stream of simpleTwitter object
		DataStream<String> RealTimeFirstStreamTweets = env.addSource(twitterFirstStream);


        DataStream<String> HistoryJson = env.readTextFile(historyLiveJsonFilePath);


        DataStream<String> AllJson = HistoryJson
                    .union(RealTimeFirstStreamTweets)
                    .union(RealTimeSecondStreamTweets);

        DataStream<Tweet> streamRealTimeTweets = AllJson.map(new TweetFromJson());


        //merge both flow
        DataStream<Tweet> AllTweets = FlowHistoryTweets.union(streamRealTimeTweets);


        DataStream<NameAndCount> streamTwittos = AllTweets
                .filter(new RemoveEmptyTweet())
                .map(new MapFunction<Tweet, NameAndCount>() {
                    @Override
                    public NameAndCount map(Tweet simpleTwitter) throws Exception {
                        return new NameAndCount(simpleTwitter.getTwitterName(), 1);
                    }
                })
                .filter(new RemoveEmptyNameAndCount())
                .filter(new RemoveFakeTwitter())
                    // group by words and sum their occurrences
                .keyBy(0).sum(1);

        DataStream<NameAndCount> streamTwits = AllTweets
                .map(new MapFunction<Tweet, String>() {
                    @Override
                    public String map(Tweet simpleTwitter) throws Exception {
                        return simpleTwitter.getTwittText();
                    }
                })
                //.timeWindowAll(Time.of(5, TimeUnit.SECONDS), Time.of(1, TimeUnit.SECONDS))
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
        DataStream<Tuple2<String, Integer>> streamGeo = streamRealTimeTweets
                .map(new MapFunction<Tweet, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(Tweet simpleTwitter) throws Exception {
                        return new Tuple2<String, Integer>(simpleTwitter.getGeo(),1);
                    }
                })
                // group by words and sum their occurrences
                .keyBy(0).sum(1);

        DataStream<Tuple2<String, Integer>> streamCoordinate = streamRealTimeTweets
                .map(new MapFunction<Tweet, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(Tweet simpleTwitter) throws Exception {
                        return new Tuple2<String, Integer>(simpleTwitter.getCoordinate(),1);
                    }
                })
                // group by words and sum their occurrences
                .keyBy(0).sum(1);

        */
        RealTimeSecondStreamTweets.writeAsText("/tmp/"+dedicatedSecondStreamOutputFileName, FileSystem.WriteMode.OVERWRITE);
        RealTimeFirstStreamTweets.writeAsText("/tmp/" + dedicatedFirstStreamOutputFileName, FileSystem.WriteMode.OVERWRITE);

        AllJson.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        //streamRealTimeTweets.print();
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



}
