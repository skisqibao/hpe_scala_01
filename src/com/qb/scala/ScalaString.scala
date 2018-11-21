package com.qb.scala

object ScalaString {
  def main(args: Array[String]): Unit = {
    //定义字符串
    val str1 = "Hello Scala"
    var str2 = "Hello Scala"
    var str2_1 = "hello scala"

    println(str1 == str2)
    println(str1.equals(str2))
    println(str1.equalsIgnoreCase(str2_1))

    println(str1.compareTo(str2_1))
    println(str1.compareToIgnoreCase(str2_1))
    println(str1.charAt(6))
    println(str2.concat(" Language"))
    println(str1.endsWith("la"))
    println(str1.getBytes)

    println(str1.hashCode)
    println(str1.indexOf("ca"))
    println(str1.intern())
    println(str1.lastIndexOf("al"))
    println(str1.length)
    println(str1.matches("d+"))
    println(str1.replace('a','o'))
    println(str1.split(" ")(1))
    println(str1.startsWith("Hel"))
    println(str1.substring(3))
    println(str1.substring(3,7))
    println(str1.toLowerCase())
    println(str1.toUpperCase())
    println(str1.trim)


    val strBuilder = new StringBuilder
    strBuilder.append("Hello ")
    strBuilder.append("Scala")
    println(strBuilder)
    println(strBuilder.reverse)
    println(strBuilder.capacity);
    println(strBuilder.insert(6,"Spark "));

  }
}
