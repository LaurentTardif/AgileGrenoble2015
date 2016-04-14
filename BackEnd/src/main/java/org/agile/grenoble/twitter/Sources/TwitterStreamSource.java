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

package org.agile.grenoble.twitter.Sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.common.DelimitedStreamReader;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.processor.HosebirdMessageProcessor;
import org.apache.flink.api.java.ClosureCleaner;
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
import org.apache.flink.api.common.functions.StoppableFunction ;
/**
 * Implementation of {@link SourceFunction} specialized to emit tweets from
 * Twitter. This is not a parallel source because the Twitter API only allows
 * two concurrent connections.
 */
public class TwitterStreamSource
			extends RichSourceFunction<String>
			implements StoppableFunction {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamSource.class);

	private static final long serialVersionUID = 1L;

	// ----- Required property keys

	public static final String CONSUMER_KEY = "twitter-source.consumerKey";

	public static final String CONSUMER_SECRET = "twitter-source.consumerSecret";

	public static final String TOKEN = "twitter-source.token";

	public static final String TOKEN_SECRET = "twitter-source.tokenSecret";

	// ------ Optional property keys

	public static final String CLIENT_NAME = "twitter-source.name";

	public static final String CLIENT_HOSTS = "twitter-source.hosts";

	public static final String CLIENT_BUFFER_SIZE = "twitter-source.bufferSize";


	private String authPath;
	protected transient BlockingQueue<String> queue;
	protected int queueSize = 10000;

	// ----- Fields set by the constructor

	private final Properties properties = new Properties();

	//private EndpointInitializer initializer = new SampleStatusesEndpoint();
	private transient BasicClient client;
	private transient volatile boolean running = false;
	private transient Object waitLock;

	private int waitSec = 5;

	private int maxNumberOfTweets;
	private int currentNumberOfTweets;



	/**
	 * Create {@link TwitterStreamSource} for streaming
	 * 
	 * @param authPath
	 *            Location of the properties file containing the required
	 *            authentication information.
	 */
	public TwitterStreamSource(String authPath) {
		this.authPath = authPath;
		loadAuthenticationProperties();

		maxNumberOfTweets = -1;
	}

	/**
	 * Reads the given properties file for the authentication data.
	 *
	 * @return the authentication data.
	 */
	private void loadAuthenticationProperties() {

		try (InputStream input = new FileInputStream(authPath)) {
			properties.load(input);
		} catch (Exception e) {
			throw new RuntimeException("Cannot open .properties file: " + authPath, e);
		}

	}



	// ----- Source lifecycle

	@Override
	public void open(Configuration parameters) throws Exception {
		waitLock = new Object();
	}

    public  void configEndpoint(StatusesFilterEndpoint endpoint) {
        LOG.info("-------------------------------------") ;
        LOG.info("-------------------------------------") ;
        LOG.info("BAD CONFIG END POINT");
        LOG.info("-------------------------------------") ;
        LOG.info("-------------------------------------") ;

    }

	@Override
	public void run(final SourceContext<String> ctx) throws Exception {
		//LOG.info("Initializing Twitter Streaming API connection : creating endpoint");
        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(CONSUMER_KEY) );
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.stallWarnings(false);
        endpoint.delimited(false);
        configEndpoint(endpoint);

        LOG.info("Initializing Twitter Streaming API connection : creating auth");
		Authentication auth = new OAuth1(properties.getProperty(CONSUMER_KEY),
				properties.getProperty(CONSUMER_SECRET),
				properties.getProperty(TOKEN),
				properties.getProperty(TOKEN_SECRET));

        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(CONSUMER_KEY) );
        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(CONSUMER_SECRET) );
        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(TOKEN) );
        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(TOKEN_SECRET) );
        //LOG.info("Initializing Twitter Streaming API connection => " + properties.getProperty(CLIENT_HOSTS, Constants.STREAM_HOST) );
        //LOG.info("Initializing Twitter Streaming API connection (end point)=> " + endpoint.getURI() + ", API VERISON" + endpoint.getHttpMethod() );
        //LOG.info("Initializing Twitter Streaming API connection (auth)=> " + auth);


        client = new ClientBuilder()
                .name("TwitterFilterSource")
                .hosts(properties.getProperty(CLIENT_HOSTS, Constants.STREAM_HOST))
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new HosebirdMessageProcessor() {
                    public DelimitedStreamReader reader;

                    @Override
                    public void setup(InputStream input) {
                        reader = new DelimitedStreamReader(input, Constants.DEFAULT_CHARSET, Integer.parseInt(properties.getProperty(CLIENT_BUFFER_SIZE, "50000")));
                    }

                    @Override
                    public boolean process() throws IOException, InterruptedException {
                        String line = reader.readLine();
						if (line != null && !line.isEmpty()) ctx.collect(line);
						LOG.info("Collecting a line from twitter : (" + line +")") ;
						return true;
					}
				})
				.build();
       //LOG.info("------------------------------");
        //LOG.info("Twitter Streaming API connection : doing the connection to " + client.toString()  + ","+ client.getEndpoint().getURI());
        //LOG.info("hosts :" + client.getName());
		client.connect();
		running = true;

		LOG.info("Twitter Streaming API connection established successfully");

		// just wait now
		while(running) {
			synchronized (waitLock) {
                //need to configure this one
				waitLock.wait(200L);
			}
		}
	}

	@Override
	public void close() {
		this.running = false;
		LOG.info("Closing source");
		if (client != null) {
			// client seems to be thread-safe
			client.stop();
		}
		// leave main method
		synchronized (waitLock) {
			waitLock.notify();
		}
	}

	@Override
	public void cancel() {
		LOG.info("Cancelling Twitter source");
		close();
	}

	@Override
	public void stop() {
		LOG.info("Stopping Twitter source");
		close();
	}

	/*
	public interface EndpointInitializer {
		StreamingEndpoint createEndpoint();
	}
    private static class SampleStatusesEndpoint implements EndpointInitializer, Serializable {
		@Override
		public StreamingEndpoint createEndpoint() {
			// this default endpoint initializer returns the sample endpoint: Returning a sample from the firehose (all tweets)
			StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
			endpoint.stallWarnings(false);
			endpoint.delimited(false);
			return endpoint;
		}
	}
*/

}
