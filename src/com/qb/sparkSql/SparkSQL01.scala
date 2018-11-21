package com.qb.sparkSql

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SparkSQL01 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local").setAppName("SparkSQL01")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")

    //创建SparkSQL上下文
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read.format("json").load("")

  }
}
