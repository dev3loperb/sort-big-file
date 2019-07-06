package com.github.ipergenitsa

import java.io.File
import java.io.FileWriter

import scala.util.Random

object InputGenerator extends App {
  val linesNumber = args(0).toInt
  val maxLength = args(1).toInt

  val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890+-!.,"

  private val file = new File("dir/generated.txt")
  file.getParentFile.mkdirs()
  val output = new FileWriter(file)
  1 to linesNumber foreach { _ =>
    val length = Random.nextInt(maxLength - 1) + 1
    val buffer = new StringBuilder(length)
    1 to length foreach { _ =>
      buffer.append(chars.charAt(Random.nextInt(chars.length)))
    }
    output.write(buffer.toString)
    output.write('\n')
  }
  output.close()
}
