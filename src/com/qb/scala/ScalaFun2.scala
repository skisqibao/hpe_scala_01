package com.qb.scala

import java.util.Date

object ScalaFun2 {
  def main(args: Array[String]): Unit = {
    println(highFun1(temF1, 1))

    var fun = highFun2(1)
    println(fun(1, 1))
    println(highFun2(1)(1, 1))
    println("111111111111111111111111111111111")

    var res = highFun4((a: Int, b: Int) => a + b, 1)(1)
    println(res)

    var res1 = highFun4(_ + _, 2)(2)
    println(res1)

    var fun1 = highFun4((a: Int, b: Int) => a + b, 2)
    var res0 = fun1(3)
    println(res0)
  }

  //高阶函数
  //参数是函数
  def highFun1(f: (Int) => Int, num: Int) = f(num)

  def temF1(num: Int) = num + 1


  //返回是函数
  def highFun2(num: Int): (Int, Int) => Double = {
    def temF2(num1: Int, num2: Int): Double = {
      num + num1 + num2
    }

    temF2
  }

  def highFun3(num: Int): (Int, Int) => Double = {
    (num1: Int, num2: Int) => num1 + num2 + num
  }

  //参数和返回都是函数
  def highFun4(f: (Int, Int) => Int, num1: Int): (Int) => Double = {
    (num: Int) => num + f(num1, 1)
  }

  def highFun5(num: Int): (Int) => Int = {
    def fun(a: Int) = num + a
    fun
  }

  //柯里化函数
  def klhFun(a: Int)(b: Int) = a + b

  //普通函数
  def log(date: Date, content: String) = {
    println("date" + date + "\tcontent" + content)
  }

  val date = new Date()
  log(date, "log1")
  log(date, "log2")
  log(date, "log3")

  //偏应用函数
  val logBoundDate = log(date, _: String)
  logBoundDate("log1_1")
  logBoundDate("log2_1")
  logBoundDate("log3_1")
}

