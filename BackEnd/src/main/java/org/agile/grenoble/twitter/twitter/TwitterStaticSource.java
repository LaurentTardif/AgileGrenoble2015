package org.agile.grenoble.twitter.twitter;
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

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.DefaultStreamingEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.DefaultStreamingEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

/**
 * Implementation of {@link SourceFunction} specialized to emit tweets from
 * Twitter. This is not a parallel source because the Twitter API only allows
 * two concurrent connections.
 */
public class TwitterStaticSource extends RichSourceFunction<String> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamSource.class);

    private static final long serialVersionUID = 1L;
    private static final String TWITTER_STATIC =  "https://api.twitter.com/";
    private String authPath;
    protected transient BlockingQueue<String> queue;
    protected int queueSize = 10000;
    private transient BasicClient client;
    private int waitSec = 5;

    private int maxNumberOfTweets;
    private int currentNumberOfTweets;

    private transient volatile boolean isRunning;

    /**
     * Create {@link TwitterStreamSource} for streaming
     *
     * @param authPath
     *            Location of the properties file containing the required
     *            authentication information.
     */
    public TwitterStaticSource(String authPath) {
        this.authPath = authPath;
        maxNumberOfTweets = -1;
    }

    /**
     * Create {@link TwitterStreamSource} to collect finite number of tweets
     *
     * @param authPath
     *            Location of the properties file containing the required
     *            authentication information.
     * @param numberOfTweets
     *
     */
    public TwitterStaticSource(String authPath, int numberOfTweets) {
        this.authPath = authPath;
        this.maxNumberOfTweets = numberOfTweets;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        initializeConnection();
        currentNumberOfTweets = 0;
        isRunning = true;
    }

    /**
     * Initialize Hosebird Client to be able to consume Twitter's Streaming API
     */
    protected void initializeConnection() {

        if (LOG.isInfoEnabled()) {
            LOG.info("Initializing TwitterStaticSource connection");
        }

        queue = new LinkedBlockingQueue<String>(queueSize);

        SearchEndPoint endpoint = new SearchEndPoint();
        //endpoint.setQueryTerm("ag15");
        //endpoint.stallWarnings(false);

        Authentication auth = authenticate();

        initializeClient(endpoint, auth);

        if (LOG.isInfoEnabled()) {
            LOG.info("TwitterStaticSource established successfully");
        }
    }

    protected OAuth1 authenticate() {

        Properties authenticationProperties = loadAuthenticationProperties();
        if (LOG.isInfoEnabled()) {
            LOG.info("auth used are : " + authenticationProperties.getProperty("consumerKey"));
        }
        return new OAuth1(authenticationProperties.getProperty("consumerKey"),
                authenticationProperties.getProperty("consumerSecret"),
                authenticationProperties.getProperty("token"),
                authenticationProperties.getProperty("secret"));
    }

    /**
     * Reads the given properties file for the authentication data.
     *
     * @return the authentication data.
     */
    private Properties loadAuthenticationProperties() {

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(authPath)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open .properties file: " + authPath, e);
        }
        return properties;
    }

    protected void initializeClient(SearchEndPoint endpoint, Authentication auth) {

        client = new ClientBuilder().name("TwitterStaticSource").hosts(TWITTER_STATIC)
                .endpoint(endpoint).authentication(auth)
                .processor(new StringDelimitedProcessor(queue)).build();

        client.connect();
    }

    @Override
    public void close() {

        if (LOG.isInfoEnabled()) {
            LOG.info("Initiating connection close");
        }

        if (client != null) {
            client.stop();
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Connection closed successfully");
        }
    }

    /**
     * Get the size of the queue in which the tweets are contained temporarily.
     *
     * @return the size of the queue in which the tweets are contained
     *         temporarily
     */
    public int getQueueSize() {
        return queueSize;
    }

    /**
     * Set the size of the queue in which the tweets are contained temporarily.
     *
     * @param queueSize
     *            The desired value.
     */
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * This function tells how long TwitterStreamSource waits for the tweets.
     *
     * @return Number of second.
     */
    public int getWaitSec() {
        return waitSec;
    }

    /**
     * This function sets how long TwitterStreamSource should wait for the tweets.
     *
     * @param waitSec
     *            The desired value.
     */
    public void setWaitSec(int waitSec) {
        this.waitSec = waitSec;
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        while (isRunning) {
            if (client.isDone()) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Client connection closed unexpectedly: {}", client.getExitEvent()
                            .getMessage());
                }
                break;
            }

            ctx.collect(queue.take());

            if (maxNumberOfTweets != -1 && currentNumberOfTweets >= maxNumberOfTweets) {
                break;
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
