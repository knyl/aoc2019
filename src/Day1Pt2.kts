#!/usr/bin/env kscript

import java.io.File

fun calculateFuel(mass: Int): Int {
    val fuel = (mass / 3).toInt() - 2
    if (fuel < 0)
        return 0
    else
        return fuel + calculateFuel(fuel)
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

