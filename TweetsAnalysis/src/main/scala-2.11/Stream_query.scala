/**
  * Created by atanu on 11/10/16.
  */

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.json4s.DefaultFormats

object Stream_query {
  def main(args: Array[String]): Unit = {


    implicit val formats = DefaultFormats
    val conf = new SparkConf().setMaster("local").setAppName("wordcount")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val tweettable = sqlContext.read.json("/media/atanu/New Volume/Tweet-analysis/streamingData.json")
    tweettable.printSchema()

    tweettable.createTempView("election")

    val location= sqlContext.sql("SELECT user.location, user.id, created_at FROM election WHERE user.location IS NOT null").rdd
    val locTweets= location.filter( row=>
      (row.getString(2).toLowerCase.contains("donald") || row.getString(2).toLowerCase.contains("trump")
        || row.getString(2).toLowerCase.contains("hillary")
        || row.getString(2).toLowerCase.contains("clinton")
        || row.getString(2).toLowerCase.contains("makeamericagreatagain")
        || row.getString(2).toLowerCase.contains("vote"))
        || row.getString(2).toLowerCase.contains("whitehouse")
        || row.getString(2).toLowerCase.contains("michelle")
        || row.getString(2).toLowerCase.contains("obama")
    )
    location.take(50).foreach(println)
    locTweets.saveAsTextFile("/home/atanu/Documents/PBPhase2/RDD4-_Locations")



    //val stream_query = sqlContext.sql(" SELECT  created_at , id from election   ")
    //stream_query.show()
    //stream_query.

  }
}
