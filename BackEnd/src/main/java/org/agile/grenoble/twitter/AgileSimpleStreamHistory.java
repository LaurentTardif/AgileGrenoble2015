package org.agile.grenoble.twitter;

import org.agile.grenoble.twitter.Mappers.TweetFromJson;
import org.agile.grenoble.twitter.Mappers.TweetFromTuple;
import org.agile.grenoble.twitter.filters.*;
import org.agile.grenoble.twitter.streamData.NameAndCount;
import org.agile.grenoble.twitter.streamData.Tweet;
import org.agile.grenoble.twitter.Mappers.TokenizeFlatMap;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adminpsl on 30/11/15.
 */
public class AgileSimpleStreamHistory {

    private static boolean fileInput = false;
    private static boolean fileOutput = false;
    /** path to the twitter properties   */
    private static String propertiesPath;
    private static String specificSuffix = null ;

    private static final Logger LOG = LoggerFactory.getLogger(AgileDoubleStreamLive.class);




    public static void main(String[] args) {
        if (!parseParameters(args)) {
            LOG.error("Arguments fail!");
            System.exit (1);
        };
        String outputPathPrefix = " /tmp/out.txt" ;
        String historyTupleFilePath = "/tmp/historyTuple.txt";
        String historyLiveJsonFilePath = "/tmp/historyLive.json";
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        process(outputPathPrefix, historyTupleFilePath, historyLiveJsonFilePath, env);
    }



    static void process(String outputPathPrefix, String historyTupleFilePath, String historyLiveJsonFilePath, StreamExecutionEnvironment env) {
        if (specificSuffix!= null && !StringUtils.isEmpty(specificSuffix)) {
            historyTupleFilePath+="."+specificSuffix;
            historyLiveJsonFilePath+="."+specificSuffix;
            outputPathPrefix+="."+specificSuffix;
        }


        DataStream<String> ListHistoryTuple = env.readTextFile(historyTupleFilePath);
        DataStream<Tweet> FlowHistoryTweets = ListHistoryTuple.map(new TweetFromTuple());


        DataStream<String> HistoryJson = env.readTextFile(historyLiveJsonFilePath);


        DataStream<String> AllJson = HistoryJson ;

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

        AllJson.writeAsText(outputPathPrefix, FileSystem.WriteMode.OVERWRITE);

        streamTwittos.writeAsText(outputPathPrefix + ".twittos", FileSystem.WriteMode.OVERWRITE);
        streamTwits.writeAsText(outputPathPrefix+".twits", FileSystem.WriteMode.OVERWRITE);

        try {
            if (LOG.isInfoEnabled()) {
                LOG.info("Twitter Streaming API tracking AGILE in progress");
            }
            env.execute("Twitter Streaming Test");
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Twitter Streaming API tracking AGILE in progress",e);
            }
        }
    }

    private static boolean parseParameters(String[] args) {
        if (args.length > 0) {
            // parse input arguments
            fileOutput = true;
            fileInput = true ;
            if (args.length == 2) {
                propertiesPath = args[0];
                specificSuffix =args[1];
            } else if (args.length == 1) {
                propertiesPath = args[0];
            } else {
                System.err.println("USAGE:\nTwitterStream [<pathToPropertiesFile>] <result path>");
                return false;
            }
        } else {
            LOG.info("Executing TwitterStream example with built-in default data.");
            LOG.info("  Provide parameters to read input data from a file.");
            LOG.info("  USAGE: TwitterStream [<pathToPropertiesFile>] <result path>");
        }
        return true;
    }



}

