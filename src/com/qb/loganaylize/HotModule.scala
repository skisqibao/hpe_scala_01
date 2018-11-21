package com.qb.loganaylize

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.commons.io.FileUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object HotModule {
  def main(args: Array[String]): Unit = {
    //创建配置对象
    val conf = new SparkConf()
    conf.setAppName("HotModule")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val file = new File("f:/datalog/HotModule")
    if(file.exists()){
      FileUtils.deleteDirectory(file)
    }
    //读取本地文件,Spark读取本地文件一个切片的默认大小为32M
    val logRDD = sc.textFile("f:/logdata")
    //过滤脏数据
    val filterRDD = logRDD.map(_.split("\t"))
            .filter(_.length == 5)

    val resultRDD = hotChannel(filterRDD)

    resultRDD.saveAsTextFile("f:/datalog/HotModule")
    sc.stop()
  }

  def hotChannel(filterRDD:RDD[Array[String]]) = {
    val pageView = channelPV(filterRDD)
    val userView = channelUV(filterRDD)

    //将两个RDD连接
    val joinRDD = pageView.join(userView)
    val resultRDD = joinRDD.map(x => {
      (x._1,x._2._1*0.3 + x._2._2*0.7)
    })
    //排序
    val sortRDD = resultRDD.sortBy(_._2, false)
    //返回
    sortRDD
  }



  def channelPV(filterRDD:RDD[Array[String]]) = {
    //设置格式:(date_channel,1)
    val usefulRDD = filterRDD.map(x => {
      val timestamp = x(2).toLong
      val date = new Date(timestamp)
      val format = new SimpleDateFormat("yyyy-MM-dd")
      val dateStr = format.format(date)
      (dateStr + "_" + x(4), 1)
    })
    //对date-channel相同的Key进行累加
    val pageViewRDD = usefulRDD.reduceByKey(_+_)
    //返回
    pageViewRDD
  }

  def channelUV(filterRDD:RDD[Array[String]]) = {
    //设置格式:(date_channel_userId)
    val usefulRDD = filterRDD.map(x => {
      val timestamp = x(2).toLong
      val date = new Date(timestamp)
      val format = new SimpleDateFormat("yyyy-MM-dd")
      val dateStr = format.format(date)
      (dateStr + "_" + x(4) + "_" + x(1))
    })

    //调用distinct(=map+reduceByKey+map)去重
    val distinctRDD = usefulRDD.distinct()

    //去重后去掉key中无用部分--userId
    val subRDD= distinctRDD.map(x => {
      (x.substring(0,x.lastIndexOf('_')),1)
    })

    //对相同key(date_channel)进行累加
    val userViewRDD = subRDD.reduceByKey(_+_)
    //返回
    userViewRDD
  }


}
