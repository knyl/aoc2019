#!/usr/bin/env kscript

import java.io.File
import kotlin.math.abs

data class Coord(var x: Int, var y: Int, var z: Int)
data class Moon(var pos: Coord, var vel: Coord)
data class Position(val p1: Int, val p2: Int, val p3: Int, val p4: Int, val v1: Int, val v2: Int, val v3: Int, val v4: Int)

fun addCoord(c1: Coord, c2: Coord): Coord {
    return Coord(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z)
}

fun gravity(p1: Int, p2: Int): Int {
    if (p1 > p2)
        return -1
    else if (p1 == p2)
        return 0
    else
        return 1
}

fun gravity(m: Coord, n: Coord): Coord {
    return Coord(gravity(m.x, n.x), gravity(m.y, n.y), gravity(m.z, n.z))
}

fun energy(c: Coord): Int {
    return abs(c.x) + abs(c.y) + abs(c.z)
}

fun print(m: Moon): String {
    return "pos=${printCoord(m.pos)}, vel=${printCoord(m.vel)}"
}

fun printCoord(c: Coord): String {
    return "<x=${c.x}, y=${c.y}, z=${c.z}>"
}

fun calc(moons: List<Moon>): List<Moon> {
    val newMoons = mutableListOf<Moon>()
    val grav = MutableList(moons.size) { Coord(0, 0, 0) }
    for (i in moons.indices) {
        val m = moons[i]
        for (j in i + 1 until moons.size) {
            val n = moons[j]
            grav[i] = addCoord(grav[i], gravity(m.pos, n.pos))
            grav[j] = addCoord(grav[j], gravity(n.pos, m.pos))
        }

        // Apply gravity
        val newVel = Coord(m.vel.x + grav[i].x, m.vel.y + grav[i].y, m.vel.z + grav[i].z)
        val newPos = Coord(m.pos.x + newVel.x, m.pos.y + newVel.y, m.pos.z + newVel.z)
        newMoons.add(Moon(newPos, newVel))
    }
    return newMoons.toList()
}

fun findPeriodicity(moons: List<Moon>, get: (Coord) -> Int): Pair<Int, Int> {
    var step = 0
    var periodicity = false
    var period = 0
    var start = 0
    val data = mutableMapOf<Position, Int>()
    var xMoons = moons.toList()
    data[Position(get(moons[0].pos), get(moons[1].pos), get(moons[2].pos), get(moons[3].pos), get(moons[0].vel), get(moons[1].vel), get(moons[2].vel), get(moons[3].vel))] = 0

    while (!periodicity) {
        step++
        xMoons = calc(xMoons)

        val currPos = Position(get(xMoons[0].pos), get(xMoons[1].pos), get(xMoons[2].pos), get(xMoons[3].pos), get(xMoons[0].vel), get(xMoons[1].vel), get(xMoons[2].vel), get(xMoons[3].vel))
        if (data.containsKey(currPos)) {
            periodicity = true
            period = step
            start = data[currPos]!!
        }
    }
    return Pair(start, period)
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val input: List<String> = File(fileName).readLines()

val pattern = "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>".toRegex()

val moons = input.map {
    val (x, y, z) = pattern.find(it)!!.destructured
    Moon(Coord(x.toInt(), y.toInt(), z.toInt()), Coord(0, 0, 0))
}

val xPeriod = findPeriodicity(moons, {c:Coord -> c.x})
val yPeriod = findPeriodicity(moons, {c:Coord -> c.y})
val zPeriod = findPeriodicity(moons, {c:Coord -> c.z})


System.out.println("Result: " + xPeriod)
System.out.println("Result: " + yPeriod)
System.out.println("Result: " + zPeriod)

