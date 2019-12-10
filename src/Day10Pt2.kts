#!/usr/bin/env kscript

import java.io.File
import kotlin.math.pow

fun slope(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Double {
    if (p1.first - p2.first == 0 && p2.second > p1.second)
        return Double.POSITIVE_INFINITY
    if (p1.first - p2.first == 0)
        return Double.NEGATIVE_INFINITY
    return (p2.second - p1.second).toDouble() / (p2.first - p1.first).toDouble()
}

fun dist(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Double {
    // skipping sqrt here, doesn't really matter anyway
    return (p2.first - p2.first).toDouble().pow(2) + (p2.second - p1.second).toDouble().pow(2)
}


if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val input: List<String> = File(fileName).readLines()

val points = mutableSetOf<Pair<Int, Int>>()

val station = Pair(23, 20)
//val station = Pair(11, 13)

input.mapIndexed { i, line ->
    line.mapIndexed { j, c ->
        if ('#' == c && !(station.first == j && station.second == i))
            points.add(Pair(j, i))
    }
}

/*
  Laser starts up and goes clockwise => divide A and B at x-coord
  Sort pairs according to angle and distance. Each angle contains a list with distances
 */

val dataA = mutableMapOf<Double, MutableList<Pair<Pair<Int, Int>, Double>>>()
val dataB = mutableMapOf<Double, MutableList<Pair<Pair<Int, Int>, Double>>>()

points.forEach { p ->
    val slope = slope(station, p)
    val dist = dist(station, p)
    if (p.first < station.first) {
        val distances = dataA.getOrDefault(slope, mutableListOf())
        distances.add(Pair(p, dist))
        dataA[slope] = distances
    } else {
        val distances = dataB.getOrDefault(slope, mutableListOf())
        distances.add(Pair(p, dist))
        dataB[slope] = distances
    }
}
dataA.forEach { entry ->
    dataA[entry.key] = entry.value.sortedBy { it.second }.toMutableList()
}
dataB.forEach { entry ->
    dataB[entry.key] = entry.value.sortedBy { it.second }.toMutableList()
}


var count = 0
var res = Pair(0, 0)

while (count < 200) {
    val toBeRemovedA =  mutableListOf<Double>()
    val toBeRemovedB =  mutableListOf<Double>()
    for (key in dataB.keys.sorted()) {
        val removedPair = dataB[key]!!.removeAt(0)
        if (dataB[key]!!.isEmpty()) {
            toBeRemovedB.add(key)
        }
        count++
        if (count == 200) {
            res = removedPair.first
        }
    }
    toBeRemovedB.forEach { dataB.remove(it) }
    for (key in dataA.keys.sorted()) {
        val removedPair = dataA[key]!!.removeAt(0)
        if (dataA[key]!!.isEmpty()) {
            toBeRemovedA.add(key)
        }
        count++
        if (count == 200) {
            res = removedPair.first
        }
    }
    toBeRemovedA.forEach { dataA.remove(it) }
}

System.out.println("Result: " + res + " " + (res.first * 100 + res.second))

