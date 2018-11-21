package com.qb.loganaylize

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.hadoop.util.hash.Hash
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object PageView {
  def main(args: Array[String]): Unit = {
    //创建配置对象
    val conf = new SparkConf()
    conf.setAppName("PageView")
    conf.setMaster("local")
    val sc = new SparkContext(conf)

    //读取本地文件,Spark读取本地文件一个切片的默认大小为32M
    val logRdd = sc.textFile("f:/logdata")

    //注意:以下思路存在OOM

    //将每一条数据切分,并将脏数据过滤
    val filterRDD = logRdd.map(_.split("\t"))
      .filter(x => x.length == 5)



    sc.stop()
  }
}
