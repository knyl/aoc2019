#!/usr/bin/env kscript

import java.io.File

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val input: List<String> = File(fileName).readLines()

val points = mutableSetOf<Pair<Int, Int>>()

input.mapIndexed { i, line ->
    line.mapIndexed { j, c ->
        if ('#' == c)
            points.add(Pair(j, i))
    }
}

val collisionsA = mutableMapOf<Pair<Int, Int>, MutableSet<Double>>()
val collisionsB = mutableMapOf<Pair<Int, Int>, MutableSet<Double>>()

points.forEach { p1 ->
    points.forEach { p2 ->
        if (p1 != p2) {
            val slope = (p1.second - p2.second).toDouble() / (p1.first - p2.first).toDouble()
            if (p1.second >= p2.second) {
                val colls = collisionsA.getOrDefault(p1, mutableSetOf())
                colls.add(slope)
                collisionsA.put(p1, colls)
            } else {
                val colls = collisionsB.getOrDefault(p1, mutableSetOf())
                colls.add(slope)
                collisionsB.put(p1, colls)
            }
        }
    }
}
val totalCollisions = points.map { p ->
    val collisions = collisionsA.getOrDefault(p, mutableSetOf()).size + collisionsB.getOrDefault(p, mutableSetOf()).size
    Pair(p, collisions)
}

val result = totalCollisions.maxBy { it.second }

System.out.println("Result: " + result)

