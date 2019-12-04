#!/usr/bin/env kscript

import Day3Pt2.ResultType
import java.io.File

typealias ResultType = Pair<Map<Pair<Int, Int>, Int>, Set<Pair<Int, Int>>>

fun createMap(instructions: List<String>): ResultType {
    var coord = Pair(0, 0)
    var set = mutableSetOf<Pair<Int, Int>>()
    var stepCount = 0
    var distances = mutableMapOf<Pair<Int, Int>, Int>()
    for (instruction in instructions) {
        val steps = instruction.substring(1).toInt()
        val places = when (instruction[0]) {
            'U' -> (coord.second..(coord.second + steps)).toList().map { y -> Pair(coord.first, y) }
            'D' -> (coord.second downTo (coord.second - steps)).toList().map { y -> Pair(coord.first, y) }
            'R' -> (coord.first..(coord.first + steps)).toList().map { x -> Pair(x, coord.second) }
            'L' -> (coord.first downTo (coord.first - steps)).toList().map { x -> Pair(x, coord.second) }
            else -> emptyList()
        }
        places.forEachIndexed { i, coord -> if (!distances.containsKey(coord)) distances[coord] = i + stepCount }
        set.addAll(places)
        stepCount += steps
        coord = places.last()
    }
    return Pair(distances, set)
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val results = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { instructions -> createMap(instructions) }

val res1: ResultType = results[0]
val res2: ResultType = results[1]

val crossingsAll = res1.second.intersect(res2.second)

val crossings = crossingsAll.minus(Pair(0, 0))

val distance = crossings.fold(Int.MAX_VALUE) { distance, coord -> if ((res1.first.getOrDefault(coord, Int.MAX_VALUE) + res2.first.getOrDefault(coord, Int.MAX_VALUE)) < distance) res1.first.getOrDefault(coord, Int.MAX_VALUE) + res2.first.getOrDefault(coord, Int.MAX_VALUE) else distance }

System.out.println("Result: " + distance)

