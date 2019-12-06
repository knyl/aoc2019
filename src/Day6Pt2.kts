#!/usr/bin/env kscript

import java.io.File

fun calculateOrbits(orbits: List<Pair<String, String>>): Int {
    val center = "COM"
    val data = mutableMapOf<String, MutableList<String>>()
    orbits.forEach {
        val list1 = data.getOrDefault(it.first, mutableListOf())
        list1.add(it.second)
        data[it.first] = list1

        val list2 = data.getOrDefault(it.second, mutableListOf())
        list2.add(it.first)
        data[it.second] = list2
    }

    val level = bfs(data, "YOU")

    return level.getOrDefault("SAN", 0) - 2
}

fun bfs(graph: Map<String, List<String>>, node: String): MutableMap<String, Int> {
    val level = mutableMapOf<String, Int>()
    val visited = mutableMapOf<String, Boolean>()
    val queue = mutableListOf<String>()
    queue.add(node)
    level[node] = 0

    visited[node] = true

    while (queue.isNotEmpty()) {
        val next = queue.removeAt(0)
        graph[next]?.forEach {
            if (!visited.getOrDefault(it, false)) {
                queue.add(it)
                level[it] = level[next]!! + 1
            }
            visited[it] = true
        }
    }
    return level
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

