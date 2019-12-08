#!/usr/bin/env kscript

import java.io.File

data class Counts(var zeroes: Int, var ones: Int, var twos: Int)

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val imageSize = 25 * 6

val input: List<String> = File(fileName).readLines()

val data = input[0]

val result = data.chunked(imageSize)
        .map {
            val zeroes = it.count { c -> c == '0' }
            val ones = it.count { c -> c == '1' }
            val twos = it.count { c -> c == '2' }
            Pair(zeroes, ones * twos)
        }
        .minBy { it.first }

System.out.println("Result: " + result.second)

