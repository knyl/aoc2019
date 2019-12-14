#!/usr/bin/env kscript

import java.io.File
import kotlin.math.abs

data class Coord(var x: Int, var y: Int, var z: Int)
data class Moon(var pos: Coord, var vel: Coord)

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

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val input: List<String> = File(fileName).readLines()

val pattern = "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>".toRegex()

var moons = input.map {
    val (x, y, z) = pattern.find(it)!!.destructured
    Moon(Coord(x.toInt(), y.toInt(), z.toInt()), Coord(0, 0, 0))
}

val steps = 1000

for (i in 0 until steps) {
    val newMoons = mutableListOf<Moon>()
    val grav = MutableList(moons.size) { Coord(0, 0, 0) }
    for (i in moons.indices) {
        val m = moons[i]
        //System.out.println("" + i + ": " + print(m))
        for (j in i+1 until moons.size) {
            val n = moons[j]
            grav[i] = addCoord(grav[i], gravity(m.pos, n.pos))
            grav[j] = addCoord(grav[j], gravity(n.pos, m.pos))
        }
        //System.out.println(grav)

        // Apply gravity
        val newVel = Coord(m.vel.x + grav[i].x, m.vel.y + grav[i].y, m.vel.z + grav[i].z)
        val newPos = Coord(m.pos.x + newVel.x, m.pos.y + newVel.y, m.pos.z + newVel.z)
        newMoons.add(Moon(newPos, newVel))
    }
    moons = newMoons
    //System.out.println()
}

val energy = moons.sumBy { energy(it.pos) * energy(it.vel) }

System.out.println("Result: " + energy)

