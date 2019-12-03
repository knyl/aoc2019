#!/usr/bin/env kscript

import java.io.File
import kotlin.math.abs

fun createMap(instructions: List<String>): Set<Pair<Int, Int>> {
    var coord = Pair(0, 0)
    var set = mutableSetOf<Pair<Int, Int>>()
    for (i in instructions) {
        val steps = i.substring(1).toInt()
        val places = when (i[0]) {
            'U' -> (coord.second..(coord.second + steps)).toList().map { y -> Pair(coord.first, y) }
            'D' -> (coord.second downTo (coord.second - steps)).toList().map { y -> Pair(coord.first, y) }
            'R' -> (coord.first..(coord.first + steps)).toList().map { x -> Pair(x, coord.second) }
            'L' -> (coord.first downTo (coord.first - steps)).toList().map { x -> Pair(x, coord.second) }
            else -> emptyList()
        }
        set.addAll(places)
        coord = places.last()
    }
    return set
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val results = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { instructions -> createMap(instructions) }

val set1: Set<Pair<Int, Int>> = results[0]
val set2: Set<Pair<Int, Int>> = results[1]

val crossingsAll = set1.intersect(set2)

val crossings = crossingsAll.minus(Pair(0, 0))

val distance = crossings.fold(Int.MAX_VALUE) { distance, coord -> if (abs(coord.first) + abs(coord.second) < distance) abs(coord.first) + abs(coord.second) else distance }

System.out.println("Result: " + distance)

