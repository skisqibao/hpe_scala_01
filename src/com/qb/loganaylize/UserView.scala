package com.qb.loganaylize

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


object UserView {
  def main(args: Array[String]): Unit = {
    //创建配置对象
    val conf = new SparkConf()
    conf.setAppName("UserView")
    conf.setMaster("local")
    val sc = new SparkContext(conf)

    //读取本地文件,Spark读取本地文件一个切片的默认大小为32M
    val logRdd = sc.textFile("f:/logdata")

    //注意:以下思路存在OOM

    //将每一条数据切分,并将脏数据过滤
    val filterRDD = logRdd.map(_.split("\t"))
      .filter(x => x.length == 5)

    /* 1. 对时间戳进行格式化.
     * 2.选取有用的数据,组成KV格式的二元组作为RDD返回
     * 3.Key为date
     * 4.Value为pageId-userId
     * (date, pageId-userId)
     */
    val usefulRDD = filterRDD.map(x => {
      val timestamp = x(2).toLong
      val date = new Date(timestamp)
      val format = new SimpleDateFormat("yyyy-MM-dd")
      val dateStr = format.format(date)
      (dateStr,x(3) + "-" + x(1))
    })
    //usefulRDD.foreach(println)

    //将KV格式的RDD按照key(date)进行分组,value是一个泛型为String的集合
    val groupRDD: RDD[(String, Iterable[String])] = usefulRDD.groupByKey()

    //对同一组内的数据,进行去重,并对pageId进行计数统计
    val restRDD = groupRDD.flatMap(x => {
      //获取date
      val date = x._1
      //获取pageId-userId
      val values = x._2

      //使用set集合进行去重
      val set = new mutable.HashSet[String]
      for(elem <- values) set.add(elem)

      //使用map对集合计数,同时去除无用的内容--userId
      val map = new mutable.HashMap[String,Int]
      for (elem <- set){
        val pageId = elem.split("-")(1)
        if(map.contains(pageId)) map.put(pageId,map.get(pageId).get+1)
        else map.put(pageId,1)
      }
      //使用一个list集合,拼接date和pageId,并与count组成一个二元组封装到list中返回
      val list = new ListBuffer[(String,Int)]
      for(elem <- map){
        val pageId = elem._1
        val count = elem._2
        list.+=((date + "_" + pageId, count))
      }
      list
    })

    //restRDD.sortBy(_._2,false).foreach(println)
    restRDD.sortBy(_._2,false).saveAsTextFile("f:/datalog/userView")

    sc.stop()
  }


}
