package com.qb.scala

import scala.collection.mutable.ListBuffer

object ScalaCollection {
  def main(args: Array[String]): Unit = {
    //创建map集合
    val map = Map(
      "1" -> "Hello",
      2 -> "Scala",
      3 -> "Spark"
    )
    //遍历map的3种方式
    map.foreach(println)

    val keyIterator = map.keys.iterator
    while (keyIterator.hasNext){
      val key = keyIterator.next()
      //get(key)返回Option类型的对象
      println(key + "--" + map.get(key).get)
    }
    println(map.get(4).getOrElse("default"))

    for(k <- map) println(k._1 + "--" + k._2)

    map.filter(x => {
        Integer.parseInt(x._1 + "") >= 2
      }).foreach(println)

      val count = map.count(x => {
        Integer.parseInt(x._1 + "") >= 2
      })
      println(count);

      //contains
      println(map.contains(2))

      //exist
      println(map.exists(x =>{
        x._2.equals("Scala")
    }))

    /*val set1 = Set(1,1,1,2,2,3)
    set1.foreach(x => print( x + "\t"))
    println("---------------")
    val set2 = Set(1,2,3,5,7)
    //求交集
    set1.intersect(set2).foreach(println)
    set1.&(set2).foreach(println)
    //求差集
    set2.diff(set1).foreach(println)
    set2.&~(set1).foreach(println)
    //求子集
    println(set1.subsetOf(set2))
    println("---------------")
    //求最大值
    println(set1.max)
    //求最小值
    println(set1.min)
    //转成List类型
    set1.toList.map(println)
    //转成字符串类型
    set1.mkString("-").foreach(print)

    println("---------------")*/

    /*val listBuffer = new ListBuffer[String]
    listBuffer.+=("hello")
    listBuffer.+=("Scala")
    listBuffer.foreach(println)

    listBuffer.-=("hello")*/


    /*Nil.foreach(println)
    val list1 = 1::2::Nil
    list1.foreach(println)*/
    /*val list = List(1,2,3,4,5)
    list.foreach(println)

    println(list.contains(6))

    list.reverse.foreach(println)
    list.take(3).foreach(println)
    list.drop(2).foreach(println)

    list.map(println)
    list.map(_ * 100).foreach(println)

    println(list.exists(_ > 4))

    list.mkString("==").foreach(print)*/

    //扁平操作
    /*val logList = "hi"::List("Hello Scala" , "Hello Spark")

    logList.map(_.split(" ")).foreach(_.foreach(println))
    val flatMapList = logList.flatMap(_.split(" "))*/


    /*val nums = new Array[Int](10)
    val strs = new Array[String](10)
    val bools = new Array[Boolean](10)

    for (index <- 0 until nums.length) nums(index) = index + 1

    nums.foreach ( (x: Int) => print(x + " ") )
    println()
    nums.foreach ( (x => print(x + " ")) )
    println()
    nums.foreach(print(_))
    println()
    nums.foreach(print)

    //二维数组
    val secArray = new Array[Array[String]](5)
    for (index <- 0 until secArray.length){
      secArray(index) = new Array[String](5)
    }

    //填充数据
    for (i <- 0 until secArray.length;j <- 0 until secArray(i).length) {
      secArray(i)(j) = i * j + ""
    }

    secArray.foreach(array => array.foreach(println))

    bools.foreach(println)*/




  }
}
