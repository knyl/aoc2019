#!/usr/bin/env kscript

import java.io.File

fun calculateFuel(mass: Int): Int {
    return (mass / 3).toInt() - 2
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val result = File(fileName).readLines()
        .map { it -> it.toInt() }
        .map { mass -> calculateFuel(mass) }
        .sum()

System.out.println("Result: $result")

