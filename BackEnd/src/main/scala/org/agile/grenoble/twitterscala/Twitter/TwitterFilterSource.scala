package org.agile.grenoble.twitterscala.Twitter

import org.agile.grenoble.twitter.{JSONParser,TwitterFilterSource}
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment,DataStream,createTypeInformation}
import org.slf4j.{LoggerFactory, Logger}
import scala.collection.JavaConverters._
import scala.collection.immutable
import java.net.URL
import util.Try


object TwitterFilterSourceScala {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[TwitterFilterSource])

  def isLink(word: String):Boolean =  {
    val result = Try { new URL(word) }.toOption match {
      case Some(theUrl) => true
      case None => false
    }
    return result
  }

  def main(args: Array[String]) {
    val propertiesPath = args(0)
    val outputPath = args(1)

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val twitterSource: TwitterFilterSource = new TwitterFilterSource(propertiesPath)
    twitterSource.trackTerms(Set("#AG15","#agile","#agileGrenoble","#agileGrenoble2015","#agilegrenoble","#agilegrenoble2015").asJava)
    twitterSource.filterLanguage("fr")

    val streamSource: DataStream[(String,String)] = env.addSource(twitterSource).flatMap((tweet,collector)=> {
        val parser = new JSONParser(tweet)
        try {
          collector.collect((parser.parse("user.name").getString("retValue"),parser.parse("text").getString("retValue")))
        }
        catch {
          case e: Exception => {
            if (LOG.isErrorEnabled) {
              LOG.error("Fail to collect name or text")
            }
            System.err.println("Fail to collect name or text")
          }
        }
      })

    val countAuthors = streamSource.map(tweet => (tweet._1,1)).keyBy(0).sum(1)
    val countWords = streamSource.map(tweet => tweet._2).
      flatMap { _.toLowerCase.split("\\s") }.
      filter(x =>  !isLink(x)).
      flatMap { _.toLowerCase.split("\\W+") }.
      filter(x => x.length()>2).
      map(word => (word,1)).keyBy(0).sum(1)
    val fullTweet = streamSource.map(tweet => tweet._2)

    countAuthors.writeAsText(outputPath + ".authors");
    countWords.writeAsText(outputPath + ".words")
    fullTweet.writeAsText(outputPath + ".fullTweet")
    try {
      if (LOG.isInfoEnabled) {
        LOG.info("Twitter Streaming API tracking AGILE in progress")
      }
      env.execute("Twitter Streaming Test")
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
  }
}

