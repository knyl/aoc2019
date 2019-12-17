#!/usr/bin/env kscript

import java.io.File

fun createPattern(originalPattern: List<Int>, step: Int): List<Int> {
    val resultList = mutableListOf<Int>()
    for (value in originalPattern)
      resultList.addAll(MutableList(step) {value})

    return resultList.toList()
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val lines: List<String> = File(fileName).readLines()

var number = lines[0].toList()

val originalPattern = listOf(0, 1, 0, -1)
val numLength = lines[0].length

for (step in 1..100) {
    val nextNumber = mutableListOf<Char>()
    for (i in 1..numLength) {
        val pattern = createPattern(originalPattern, i)
        var sum = 0

        for (j in number.indices) {
            val value = number[j].toString().toInt() * pattern[(j + 1) % pattern.size]
            sum += value
        }
        nextNumber.add(sum.toString().last())
    }
    number = nextNumber
}

System.out.println("Result: ${number.subList(0, 8).joinToString("", "", "")}")

