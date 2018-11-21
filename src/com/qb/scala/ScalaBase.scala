package com.qb.scala

//伴生类
class ScalaBase {

}

//伴生对象
object ScalaBase {

  def main(args: Array[String]): Unit = {
    //    var p1 = new Person("qb", 22, '男')
    //    p1.name = "qibao"
    //    p1.age = 23   val定义无法修改
    //    println(p1.age)
    //    println(p1.sex) 没有修饰符,无法访问
    /*var p1 = new Person("qb", 22, 99.99)
    p1.tell()
    var p2 = new Person("qb", 23)
    p2.tell()

    var p3 = Person("qqb", 22)*/

    //    println(1 until(10,3))
    var list = List("hello", "scala", "spark")
    for (index <- 0 until list.length) println(list(index))

    for (elem <- list) println(elem)

    /*for (i <- 1 to 9){
      for(j <- 1 until 10){
        if(j <= i)  print(i + "*" + j + "=" + i*j + "\t")
      }
      println()
    }*/

    /*for (i <- 1 to 9; j <- 1 to 9) {
      if (j <= i) print(i + "*" + j + "=" + i * j + "\t")
      if (j == i) println()
    }*/

    //    for (i <- 1 to 100; if i % 2 == 0) println(i)

    //    for (i <- 1 to 10 ; if i % 2 == 0 ;if i == 4 ) println(i)

    /**
      * yield 关键字
      * 存储到一个集合
      * 将符合要求的元素自动封装到一个集合中
      */
    /*val rest = for (i <- 1 to 20; if i % 2 == 0) yield i
    for (elem <- rest) println(elem)*/

    var flag = true
    var index = 0
    while (flag && index < 50){
      index += 1
      if (index == 20) flag = false
      println(index)
    }
  }
}

class Person(var name: String, var age: Int) {
  var fcp = 0.0

  //重载构造器
  def this(name: String, age: Int, facePower: Double) = {
    this(name, age)
    fcp = facePower
  }

  def tell() = println(name + ":" + age + ":" + fcp)
}

object Person {
  def apply(name: String, age: Int) = new Person(name, age)
}








