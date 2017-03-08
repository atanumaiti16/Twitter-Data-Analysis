/**
  * Created by Atanu on 10/22/16.
  */


import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s._



object demoJson {
  def main(args: Array[String]): Unit ={
    implicit val formats = DefaultFormats
    val conf = new SparkConf().setMaster("local").setAppName("wordcount")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //RDD-1 :
    val tweettable =  sqlContext.read.json ("/home/atanu/Documents/PBPhase2/CleanElections.json")
    tweettable.createTempView("election")
    val tweets = sqlContext.sql("SELECT id, user.id, text FROM election WHERE id IS NOT NULL").rdd
    val mentionHillary= tweets.filter( row=>
          (row.getString(2).toLowerCase.contains("hillary") || row.getString(2).toLowerCase.contains("clinton"))
        )


    mentionHillary.map(row =>
    row.getLong(0)+","+row.getLong(1)
    ).saveAsTextFile("/home/atanu/Documents/PBPhase2/RDD1/RDD1a_MentionHillary")
    println(mentionHillary.count()) //output: 24881

    val mentionTrump= tweets.filter( row=>
          (row.getString(2).toLowerCase.contains("donald") || row.getString(2).toLowerCase.contains("trump"))
        )
    mentionTrump.map(row =>
      row.getLong(0)+","+row.getLong(1)
    ).saveAsTextFile("/home/atanu/Documents/PBPhase2/RDD1/RDD1a_MentionTrump")
    println(mentionTrump.count()) //output: 35075
    // collect().fo.write.format("com.databricks.spark.csv").save("/home/atanu/Documents/PBPhase2/RDD1"


  //DATAFRAME - 1:
    val hashtags = sqlContext.sql("SELECT entities.hashtags.text as hashtags,COUNT(entities.hashtags.text) as count " +
      " FROM election WHERE entities.hashtags.text  IS NOT NULL GROUP BY entities.hashtags.text "+
      "ORDER BY COUNT(entities.hashtags.text) DESC " )

   val trendingHash = sqlContext.read.parquet("/home/atanu/Documents/PBPhase2/trendinghashtags.parquet")
    val filteredHash = hashtags.filter(
      row => (row.getAs[scala.collection.mutable.WrappedArray[String]]("hashtags").length != 0)
    ).select("hashtags").take(5)

//  //  val trend = filteredHash.map(r => r.getAs[String]("hashtags"))
      val listHash =   filteredHash.flatMap(r=>{
      val ff=r.get(0).toString.replace("WrappedArray(","")
      val str=ff.replace(")","")
      val space = str.replace(" ", "")
        val s=space.split(",")
//      s
    }).toList.distinct
    listHash.foreach(println)


    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/home/atanu/Documents/PBPhase2/DF1/trendinghashtags.txt",true)))
    for (x<- listHash) {
      writer.write(x + "\n")
    }
    writer.close()

   //RDD-2: location
    val location= sqlContext.sql("SELECT user.location, user.id, text FROM election WHERE user.location IS NOT null").rdd
    val locTweets= location.filter( row=>
      (row.getString(2).toLowerCase.contains("donald") || row.getString(2).toLowerCase.contains("trump")
        || row.getString(2).toLowerCase.contains("hillary")
        || row.getString(2).toLowerCase.contains("clinton")
        || row.getString(2).toLowerCase.contains("makeamericagreatagain")
        || row.getString(2).toLowerCase.contains("vote"))
    )
   
    location.take(50).foreach(println)
    locTweets.saveAsTextFile("/home/atanu/Documents/PBPhase2/RDD2_Locations")

//
//    val text= sqlContext.sql("SELECT entities.hashtags.text, COUNT(entities.hashtags.text)  from election  GROUP BY entities.hashtags.text ORDER BY  COUNT(entities.hashtags.text) DESC ")
//    text.show()
//
//    //DATAFRAME-2: media urls list
//    val media_url=sqlContext.sql("SELECT   entities.media.display_url , id from election WHERE entities.media.display_url is not null ")
//    media_url.show()
//    //trending media urls
    val media_url2=sqlContext.sql("SELECT  entities.media.display_url, count(entities.media.display_url) as CountMediaEntities  from election GROUP BY entities.media.display_url ORDER BY COUNT(entities.media.display_url) DESC")
    media_url2.show()
    media_url.write.save("/home/atanu/Documents/PBPhase2/DF2/mediaUrl")
    media_url2.write.save("/home/atanu/Documents/PBPhase2/DF2/trendingMediaUrl")

  }
}


