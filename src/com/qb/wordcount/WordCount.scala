package com.qb.wordcount

import org.apache.spark.{SparkConf, SparkContext}

object
WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WC").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")

    /*sc.textFile("f:/datalog/data/friend")
      .flatMap(_.split("\t"))
      .map((_,1))
      .reduceByKey(_+_)
      .map(_.swap)
      .sortByKey(false)
      .map(_.swap)
      .foreach(println)*/

    sc.textFile("f:/datalog/data/friend")
      .flatMap(_.split("\t"))
      .map((_,1))
      .reduceByKey(_+_)
      .sortBy(_._2,false)
      .foreach(println)

    //while(true){}
    sc.stop()
  }
}
