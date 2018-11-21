package com.qb.scala

object ScalaFun1 {
  def main(args: Array[String]): Unit = {
    println(fun1(1, 2))
    println(fun2(2, 2))
    println(fun3(2, 1))
    println(fun4(5));

    println(fun5());
    println(fun5(1));
    println(fun5(b = 2));
    println(fun5(1, 2));
    println(fun5_1(1));
    println(fun5_2(b = 2));

    println(fun6(1.0, 2.1, 3.1))

    println(fun7(1, 2));
    println(fun8(5))
  }

  def fun1(a: Int, b: Int): Int = {
    return a + b;
  }

  def fun2(a: Int, b: Int) = {
    a + b
  }

  def fun3(a: Int, b: Int) = a + b

  //递归
  def fun4(n: Int): Int = {
    if (n == 1 || n == 0) 1
    else n * fun4(n - 1)
  }

  //默认值函数
  def fun5(a: Int = 10, b: Int = 20) = a + b

  def fun5_1(a: Int, b: Int = 20) = a + b

  def fun5_2(a: Int = 10, b: Int) = a + b

  //可变参数列表
  def fun6(args: Double*) = {
    var sum = 0.0
    for (arg <- args) sum += arg
    sum
  }

  //匿名函数
  val fun7 = (a: Int, b: Int) => a + b

  //嵌套函数
  def fun8 (n:Int) = {
    def fun9(a:Int,b:Int):Int ={
      if(a==1) b
      else fun9(a-1,a*b)
    }

    fun9(n,1)
  }
}

