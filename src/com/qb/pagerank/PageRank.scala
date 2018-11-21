package com.qb.pagerank

import java.io.File

import org.apache.calcite.interpreter.Nodes
import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object PageRank {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setAppName("PageRank")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val file = new File("f:/datalog/PageRank")
    if(file.exists()){
      FileUtils.deleteDirectory(file)
    }
    //读
    val pageRDD = sc.textFile("f:/datalog/page.txt")
    //获取页面数量
    val pageNum = pageRDD.count()
    //定义阻尼系数
    val DampingCoefficient = 0.85
    //定义累加器,用于误差的计算
    /**
      * 累加器只能在Driver端定义
      * 累加器只能在Driver读取,不能在Executor端读取
      * 累加器只能在Executor端修改
      */
    var errorAccumulator = sc.accumulator(0.0)

    //将读取的页面信息进行切分,得到当前页,及其出链
    var linksRDD = pageRDD.map(x => {
      val currentPage = x.split("\t")(0)
      val nodes = x.split("\t").drop(1)
      (currentPage,Node(1.0,nodes))
    })
    var flag = true
    while(flag){
      linksRDD = linksRDD.flatMap(x => {
        //返回集合
        val voteList = new ListBuffer[(String,Double)]
        val currentPage = x._1  //当前页
        val node = x._2         //node样例类
        val curPR = node.pr     //当前页的pr值
        val nodes = node.nodes  //当前页的出链
        //计算票面值
        val outChainValue = curPR/nodes.length

        for (elem <- nodes){
          voteList.+=((elem,outChainValue))
        }
        voteList
      })
        .reduceByKey(_+_)
        .join(linksRDD)
        /**
          * join后数据格式
        (B,(1.5,Node(1.0,[Ljava.lang.String;@309dcfe9)))
        (A,(0.5,Node(1.0,[Ljava.lang.String;@24ef9b41)))
        (C,(1.5,Node(1.0,[Ljava.lang.String;@42bc21aa)))
        (D,(0.5,Node(1.0,[Ljava.lang.String;@769f6009)))
          */
        .map(x => {
        val currentPage = x._1
        val tempPR = x._2._1    //用来带入公式的临时PR
        val node = x._2._2

        //计算新的PR值
        val newPR = (1-DampingCoefficient)/pageNum + DampingCoefficient * tempPR
        val error = Math.abs(newPR - node.pr)
        node.pr = newPR
        errorAccumulator.add(error)
        (currentPage, node)
      })
      linksRDD.foreach(x =>{
        val currentPage = x._1
        val node = x._2
        println(currentPage + "\t" + node.pr)
      })
      if(errorAccumulator.value <= 0.01) flag = false
      errorAccumulator.setValue(0.0)
    }
    sc.stop()
  }
}
case class Node(var pr:Double,val nodes: Array[String])