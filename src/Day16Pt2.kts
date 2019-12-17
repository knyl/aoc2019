#!/usr/bin/env kscript

import java.io.File

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val lines: List<String> = File(fileName).readLines()

var number = lines[0].toList().map { it.toInt() - 48 }

val numLength = number.size * 10000

val messageOffset = lines[0].substring(0, 7).toInt()
System.out.println("Offset: $messageOffset")
System.out.println("numLength: $numLength")
System.out.println("numLength/offset: ${numLength.toDouble() / messageOffset}")

val list = mutableListOf<Int>()

for (i in messageOffset until numLength) {
    list.add(number[i % number.size])
}

for (step in 1..100) {
    var sum = 0
    for (i in list.indices.reversed() ) {
        val value = list[i]
        sum += value
        list[i] = sum % 10
    }
   //System.out.println(list.subList(0, 20).joinToString("", "", ""))
}

val result = list.subList(0, 8).joinToString("", "", "")
System.out.println("Result: $result")

