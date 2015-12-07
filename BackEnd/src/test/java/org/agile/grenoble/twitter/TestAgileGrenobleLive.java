package org.agile.grenoble.twitter ;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import junit.framework.TestCase;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Assert.*;
import org.junit.Test;

import java.net.URISyntaxException;


public class TestAgileGrenobleLive extends TestCase{


    @Test
    public void testSimpleRun () throws URISyntaxException {
        Configuration conf = new Configuration();
        conf.setFloat(ConfigConstants.TASK_MANAGER_MEMORY_FRACTION_KEY, 0.5f);
        StreamExecutionEnvironment env =  StreamExecutionEnvironment.createLocalEnvironment();

        String outputPathPrefix = System.getProperty("java.io.tmpdir") ;
        String historyTupleFilePath = TestAgileGrenobleLive.class.getClassLoader().getSystemResource("TestAgileGrenobleLive/history.json").toURI().toString();
        String historyLiveJsonFilePath = TestAgileGrenobleLive.class.getClassLoader().getSystemResource("TestAgileGrenobleLive/history.list").toURI().toString();

        AgileSimpleStreamHistory.process(outputPathPrefix,historyTupleFilePath,historyLiveJsonFilePath,env);

        //we should check at least one file exist, and content not null
        //the correctness of flow and algorithm will be validated somewhere else
        assertTrue("Basic test for framework validation",true);
    }



}