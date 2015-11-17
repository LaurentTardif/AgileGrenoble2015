package org.agile.grenoble.twitterscala.Twitter

//import jdk.nashorn.internal.parser.JSONParser
import org.agile.grenoble.twitter.{JSONParser,TwitterFilterSource}
import org.apache.flink.api.java.DataSet
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment,DataStream,createTypeInformation}
import org.slf4j.{LoggerFactory, Logger}
import scala.collection.JavaConverters._
import scala.collection.immutable
import java.net.URL
import util.Try

/* scala shell
   import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment,DataStream,createTypeInformation}
   import org.agile.grenoble.twitter.{JSONParser,TwitterFilterSource}
    val tw=env.readTextFile("/tmp/out.txt.fullTweet")
    val ut = tw.filter(x=>x.length()>2).map(tweet=> {
      val parser = new JSONParser(tweet)
      (parser.parse("user.name").getString("retValue"),parser.parse("text").getString("retValue"))
    })
    val gb = ut.map(tweet => (tweet._1,1)).groupBy(0).sum(1)
    gb.collect()
*/
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

    val jsonSource: DataStream[String] = env.addSource(twitterSource)
    val userTextPairSource : DataStream[(String,String)] = jsonSource.map(tweet=> {
        val parser = new JSONParser(tweet)
        (parser.parse("user.name").getString("retValue"),parser.parse("text").getString("retValue"))
      })

    val countAuthors = userTextPairSource.map(tweet => (tweet._1,1)).keyBy(0).sum(1)
    val countWords = userTextPairSource.map(tweet => tweet._2).
      flatMap { _.toLowerCase.split("\\s") }.
      filter(x =>  !isLink(x)).
      flatMap { _.toLowerCase.split("\\W+") }.
      filter(x => x.length()>2).
      map(word => (word,1)).keyBy(0).sum(1)

    countAuthors.writeAsText(outputPath + ".authors");
    countWords.writeAsText(outputPath + ".words")
    jsonSource.writeAsText(outputPath + ".fullTweet")
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

