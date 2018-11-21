package com.qb.loganaylize

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.qb.loganaylize.HotModule.hotChannel
import org.apache.commons.io.FileUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer


object ChannelTop3UserTop5 {
  def main(args: Array[String]): Unit = {
    //创建配置对象
    val conf = new SparkConf()
    conf.setAppName("HotModule")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val file = new File("f:/datalog/ModuleTop3UserTop5")
    if (file.exists()) {
      FileUtils.deleteDirectory(file)
    }
    //读取本地文件,Spark读取本地文件一个切片的默认大小为32M
    val logRDD = sc.textFile("f:/logdata")
    //过滤脏数据
    val filterRDD = logRDD.map(_.split("\t"))
      .filter(_.length == 5)

    //读取全部数据,并截取有用数据,设置格式(date_channel,userId)
    val usefulRDD = filterRDD.map(x => {
      val timestamp = x(2).toLong
      val date = new Date(timestamp)
      val format = new SimpleDateFormat("yyyy-MM-dd")
      val dateStr = format.format(date)
      (dateStr + "_" + x(4), x(1))
    })
    /**
      * usefulRDD.foreach(println)
      * (2018-11-12_python,6864)
      * (2018-09-07_spark,7892)
      */

    //得到广播变量 Broadcast[Array[ListBuffer[String]]]
    val broadcast = getBroadcast(sc, filterRDD)

    //过滤,得到每天top3模块的数据
    val filterUsefulRDD = usefulRDD.filter(x => {
      broadcast.value.contains(x._1)
    })
    //filterUsefulRDD.saveAsTextFile("f:/test/toptest")

    val userIdDateChannelRDD = filterUsefulRDD.map(x => {
      (x._2 + "_" + x._1, 1)
    })
    /**
      * userIdDateChannelRDD.foreach(println)
      */

    //累加同一个用户同一天在同一模块的访问次数,并按照访问量排序
    val reduceByKeyRDD = userIdDateChannelRDD.reduceByKey(_ + _)
      .sortBy(_._2, false)
    /**
      * reduceByKeyRDD.foreach(println)
      * (7924_2018-04-03_spark,2)
      * (6196_2018-11-29_spark,1)
      */
    //对排好序的数据再进行一把格式化,去除无用数据--count
    val date_chUserRDD = reduceByKeyRDD.map(x => {
      val userId = x._1.substring(0, x._1.indexOf('_'))
      val date_Channel = x._1.substring(x._1.indexOf('_') + 1, x._1.length)
      (date_Channel, userId)
    })
    /**
    (2018-02-13_python,5028)
    (2018-01-05_python,8845)
    (2018-02-07_scala,509)
      */

    val finalRDD = date_chUserRDD.groupByKey
                    .map(x => {
                      (x._1,x._2.take(5))
                    })

    finalRDD.sortByKey().saveAsTextFile("f:/datalog/ModuleTop3UserTop5")


  }

  private def getBroadcast(sc: SparkContext, filterRDD: RDD[Array[String]]) = {
    //得到按照热度从大到小顺序排列的所有版块信息
    val hotChannelRDD = hotChannel(filterRDD)

    val dateChannelRDD = hotChannelRDD.map(x => {
      val date = x._1.split("_")(0)
      val channel = x._1.split("_")(1)
      (date, channel)
    })

    /**
      * dateChannelRDD.foreach(println)
      * (date,channel):(2018-05-06,mr)
      */

    //对有序的dateChannelRDD进行分组,组内有序
    val dateChannelGroupRDD = dateChannelRDD.groupByKey
    /**
      * dateChannelRDD.groupByKey.foreach(println)
      * (2018-05-09,CompactBuffer(scala, mr, spark, hive, hdfs, yarn, python))
      * (2018-04-19,CompactBuffer(yarn, spark, mr, hdfs, hive, python, scala))
      */

    //获取每一天前三热的板块(date,List[Channel])
    val top3Channel = dateChannelGroupRDD.map(x => {
      (x._1, x._2.take(3))
    })
    /**
      * top3Channel.foreach(println)
      * (2018-05-09,List(scala, mr, spark))
      * (2018-04-19,List(yarn, spark, mr))
      */

    //设置存入广播变量的格式,方便后续对比
    val formatTop3Channel = top3Channel.map(x => {
      val list = new ListBuffer[String]
      for (elem <- x._2) {
        list.+=(x._1 + "_" + elem)
      }
      list
    })
    /**
      * formatTop3Channel.foreach(println)
      * ListBuffer(2018-05-09_scala, 2018-05-09_mr, 2018-05-09_spark)
      * ListBuffer(2018-04-19_yarn, 2018-04-19_spark, 2018-04-19_mr)
      */

    //将RDD转化为实际数组 Array[ListBuffer[String]]
    val top3Collect = formatTop3Channel.collect()
    //对数组格式化,将所有(2018-05-09_scala)类型的值全部放到一个List中
    val formatTop3Collect = new ListBuffer[String]
    for (elem <- top3Collect) {
      for (elemIn <- elem) {
        formatTop3Collect.+=(elemIn)
      }
    }
    //设置广播变量 Broadcast[Array[ListBuffer[String]]]
    val broadcast = sc.broadcast(formatTop3Collect)
    //返回
    broadcast
  }
}
