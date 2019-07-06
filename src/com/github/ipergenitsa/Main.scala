package com.github.ipergenitsa

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Duration
import java.time.LocalDateTime
import java.util.Objects

object Main extends App {
  val fileName = args.headOption.getOrElse {
    val default = "dir/generated.txt"
    println(s"Warning: default input file is used: $default")
    default
  }

  def splitFile(file: File, batchSize: Int): File = {
    val workingDir = new File(file.getParentFile, "batches")
    workingDir.delete()
    workingDir.mkdirs()
    val input = new BufferedReader(new FileReader(file))
    Iterator.continually(input.readLine).takeWhile(Objects.nonNull)
      .grouped(batchSize).zipWithIndex.foreach { case (lines, index) =>
      val outputFile = new FileWriter(new File(workingDir, s"$index.tmp"))
      lines.sorted.foreach(line => {
        outputFile.write(line)
        outputFile.write("\n")
      })
      outputFile.close()
    }
    workingDir
  }

  def mergeContent(dir: File): File = {
    def merge(file1: File, file2: File): Unit = {
      val outputFile = new File(file1.getParent, file1.getName + "_" + file2.getName)
      val output = new FileWriter(outputFile)
      val input1 = new BufferedReader(new FileReader(file1))
      val input2 = new BufferedReader(new FileReader(file2))
      var line1 = input1.readLine()
      var line2 = input2.readLine()
      while (line1 != null || line2 != null) {
        (line1, line2) match {
          case (s, null) => output.write(s); output.write('\n'); line1 = input1.readLine()
          case (null, s) => output.write(s); output.write('\n'); line2 = input2.readLine()
          case (s1, s2) =>
            if (s1 < s2) {
              output.write(s1)
              output.write('\n')
              line1 = input1.readLine
            } else {
              output.write(s2)
              output.write('\n')
              line2 = input2.readLine
            }
        }
      }
      output.close()
      input1.close()
      input2.close()
      file1.delete()
      file2.delete()
      outputFile.renameTo(file1)
    }

    while (dir.listFiles().filter(_.isFile).take(2).length == 2) {
      val files = dir.listFiles().filter(_.isFile).toList
      files.grouped(2).foreach {
        case x :: y :: Nil => merge(x, y);
        case _ => ()
      }
    }

    dir.listFiles().filter(_.isFile).head
  }

  val startTime = LocalDateTime.now()
  println(s"start time: $startTime")

  private val inputFile = new File(fileName)
  val splitFileLocation: File = splitFile(inputFile, 10)
  val resultFile = mergeContent(splitFileLocation).renameTo(new File(inputFile.getParent, "sorted.txt"))
  val endTime = LocalDateTime.now()

  println(s"Result file location: $resultFile")
  println(s"end time: $endTime")
  println(s"elapsed time (minutes): ${Duration.between(startTime, endTime).toMinutes}")
}
