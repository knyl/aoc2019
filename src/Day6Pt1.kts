#!/usr/bin/env kscript

import java.io.File

fun calculateOrbits(orbits: List<Pair<String, String>>): Int {
    val center = "COM"
    val data = mutableMapOf<String, MutableList<String>>()
    orbits.forEach {
        val list = data.getOrDefault(it.first, mutableListOf())
        list.add(it.second)
        data[it.first] = list
    }
    val countList = mutableMapOf<String, Int>()

    count(center, 0, countList, data)

    return countList.values.sum()
}

fun count(node: String, count: Int, countList: MutableMap<String, Int>, data: MutableMap<String, MutableList<String>>) {
    countList[node] = count
    data[node]?.forEach { count(it, count+1, countList, data) }
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val orbits = File(fileName).readLines()
        .map { it.split(")") }
        .map { Pair(it[0], it[1]) }

val result = calculateOrbits(orbits)

System.out.println("Result: $result")

