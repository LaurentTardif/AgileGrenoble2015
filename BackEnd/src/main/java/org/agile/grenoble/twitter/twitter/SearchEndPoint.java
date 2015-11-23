package org.agile.grenoble.twitter.twitter;

import com.google.common.collect.Maps;
import com.twitter.hbc.core.HttpConstants;
import com.twitter.hbc.core.endpoint.Endpoint;
import com.twitter.hbc.core.endpoint.RawEndpoint;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by adminpsl on 23/11/15.
 */
public class SearchEndPoint extends RawEndpoint {


    private static String searchURI = "/1.1/search/tweets.json?" ;

    public SearchEndPoint() {
        super(searchURI, HttpConstants.HTTP_GET);
    }

    public void setQueryTerm(String s) {
        this.addQueryParameter("q",s);
    }

}
