package com.qb.weather

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object HotWeather {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setAppName("HotWeather")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val file = new File("f:/datalog/HotWeather")
    if(file.exists()){
      FileUtils.deleteDirectory(file)
    }
    //读
    val weatherRDD = sc.textFile("f:/datalog/weather.txt")
    //过滤脏数据
    val filterRDD = weatherRDD.map(_.split("\t"))
                .filter(_.length == 2)

    val usefulRDD = filterRDD.map(x => {
      val date = x(0).split(" ")(0)
      val yearAndMonth = date.substring(0,date.lastIndexOf('-'))
      val day = date.substring(date.lastIndexOf('-')+1,date.length)
      val temperature = x(1)
      (yearAndMonth, day + "-" + temperature)
    })
    /**
      * usefulRDD.foreach(println)
      (1950-10,01-37c)
      (1951-12,01-23c)
      */
    //根据年月分组
    val groupByKeyRDD = usefulRDD.groupByKey
    /**
      * groupByKeyRDD.foreach(println)
      (1951-07,CompactBuffer(01-45c, 02-46c, 03-47c))
      (1949-10,CompactBuffer(01-34c, 01-38c, 02-36c))
      */

    //选出符合条件的天气
    val resultRDD = groupByKeyRDD.flatMap(x => {
      val yearAndMonth = x._1
      val values = x._2.toArray
      //设置一个数组,使用冒泡排序思想选出同一个月中不同天的最高温度
      val tempArray = new Array[String](2)
      tempArray(0) = values(0)
      tempArray(1) = values(0)

      //遍历天气-温度集合
      for (elem <- values){
        var flag = true
        for (i<- 0 until tempArray.length){
          if(flag){
            val eDay = elem.split("-")(0)
            val eWeather = elem.split("-")(1)

            val aDay = tempArray(i).split("-")(0)
            val aWeather = tempArray(i).split("-")(1)
            if(eWeather > aWeather){
              if(eDay == aDay){
                tempArray(i) = elem
                flag = false
              }else {
                if (i+1 < tempArray.length){
                  //不是数组的最后一个元素
                  tempArray(i+1) = tempArray(i)
                  tempArray(i) = elem
                  flag = false
                }else{
                  //是最后一个元素
                  tempArray(i) = elem
                }
              }
            }
          }
        }
      }

      /**
        * if-else可复用性差
        * 使用两层for循环
        */
      /*for (elem <- values){
        if (tempArray(0) != null || tempArray(1) != null){
          val eDay = elem.split("-")(0)
          val eWeather = elem.split("-")(1)

          val aDay0 = tempArray(0).split("-")(0)
          val aWeather0 = tempArray(0).split("-")(1)

          //同一天,保留温度较高的
          if (eDay == aDay0 && eWeather > aWeather0){
            tempArray(0) = elem
          }
          //不是同一天
          if (eDay != aDay0) {
            if(eWeather > aWeather0){
              //循环变量温度高于第一个温度,交换
              tempArray(1) = tempArray(0)
              tempArray(0) = elem
            }else if (eWeather == aWeather0){
              //循环变量温度等于第一个温度,设置第二个元素为循环变量
              tempArray(1) == elem
            }else if (eWeather < aWeather0){
              //循环变量温度低于第一个温度,再与第二个温度进行比较
              val aDay1 = tempArray(1).split("-")(0)
              val aWeather1 = tempArray(1).split("-")(1)
              //不论是否同一天,只需保留温度较高的
              if ( eWeather > aWeather1){  tempArray(1) = elem  }
            }
          }
        }
      }*/

      //设置返回类型
      val set = new mutable.HashSet[(String,String)]
      for (elem <- tempArray) {
        if (elem != null)
          set.+=((yearAndMonth,elem))
      }
      set
    })
    resultRDD.foreach(println)
    sc.stop()
  }
}
