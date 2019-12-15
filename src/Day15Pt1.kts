#!/usr/bin/env kscript

import java.io.File

data class Coord(val x: Int, val y: Int)
data class State(val c: Coord, val d: Int)

enum class TILE(val value: Int, val str: Char) {
    WALL(0, '#'), OPEN(1, '.'), START(3, 'x'), O2_SYS(2, 'o'), UNKNOWN(4, ' ');

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

enum class MOVEMENT(val value: Int) {
    NORTH(1), EAST(4), SOUTH(2), WEST(3)
}

enum class STATUS {
    RUNNING, WAITING_FOR_INPUT, HALTED, ERROR
}

class Program(program: MutableMap<Int, Int>) {
    private var nextInd = 0
    private val prog = program
    private var relBase: Int = 0

    fun get(map: MutableMap<Int, Int>, ind: Int): Int {
        return map.getOrDefault(ind, 0)
    }

    fun execute(): Pair<STATUS, Int> {
        return execute(null)
    }

    fun execute(input: Int?): Pair<STATUS, Int> {
        //System.out.println("Instruction: " + nextInd)
        while (prog[nextInd] != 99) {

            val instruction = prog[nextInd]
            val instructionStr = instruction.toString().padStart(6, '0')
            val opcode = instructionStr.substring(instructionStr.length - 2, instructionStr.length).toInt()
            //System.out.println("Instruction: $instruction")

            val mode1 = instructionStr[instructionStr.length - 3]
            val mode2 = instructionStr[instructionStr.length - 4]
            val mode3 = instructionStr[instructionStr.length - 5]
            val pos1: Int = if (mode1 == '0') get(prog, nextInd + 1) else if (mode1 == '1') nextInd + 1 else relBase + get(prog, nextInd + 1)
            val pos2: Int = if (mode2 == '0') get(prog, nextInd + 2) else if (mode2 == '1') nextInd + 2 else relBase + get(prog, nextInd + 2)

            //System.out.println("Opcode: $opcode")
            //System.out.println("Pos1 $pos1 pos2 $pos2")

            when (opcode) {
                1 -> {
                    var ind = get(prog, nextInd + 3)
                    if (mode3 == '2')
                        ind += relBase
                    prog[ind] = get(prog, pos1) + get(prog, pos2)
                    nextInd += 4
                }
                2 -> {
                    var ind = get(prog, nextInd + 3)
                    if (mode3 == '2')
                        ind += relBase
                    prog[ind] = get(prog, pos1) * get(prog, pos2)
                    nextInd += 4
                }
                3 -> {
                    if (input == null) {
                        //System.out.println("Waiting for input")
                        return Pair(STATUS.WAITING_FOR_INPUT, 0)
                    } else {
                        //System.out.println("Got input: " + input)
                    }
                    var ind = get(prog, nextInd + 1)
                    if (mode1 == '2')
                        ind += relBase
                    prog[ind] = input.toInt()
                    nextInd += 2
                }
                4 -> {
                    //res.add(get(prog, pos1))
                    //System.out.println("Output: " + get(prog, pos1))
                    nextInd += 2
                    return Pair(STATUS.RUNNING, get(prog, pos1))
                }
                5 -> {
                    if (get(prog, pos1) != 0.toInt())
                        nextInd = get(prog, pos2)
                    else
                        nextInd += 3
                }
                6 -> {
                    if (get(prog, pos1) == 0.toInt())
                        nextInd = get(prog, pos2)
                    else
                        nextInd += 3
                }
                7 -> {
                    var ind = get(prog, nextInd + 3)
                    if (mode3 == '2')
                        ind += relBase
                    if (get(prog, pos1) < get(prog, pos2))
                        prog[ind] = 1
                    else
                        prog[ind] = 0
                    nextInd += 4
                }
                8 -> {
                    var ind = get(prog, nextInd + 3)
                    if (mode3 == '2')
                        ind += relBase
                    if (get(prog, pos1) == get(prog, pos2))
                        prog[ind] = 1
                    else
                        prog[ind] = 0
                    nextInd += 4
                }
                9 -> {
                    relBase += get(prog, pos1)
                    nextInd += 2
                }
                99 -> {
                    return Pair(STATUS.HALTED, 0)
                }

                else -> return Pair(STATUS.ERROR, 0)
            }
        }

        return Pair(STATUS.HALTED, 0)
    }
}

fun getNextPos(pos: Coord, dir: MOVEMENT): Coord {
    return when (dir) {
        MOVEMENT.NORTH -> Coord(pos.x, pos.y + 1)
        MOVEMENT.EAST -> Coord(pos.x + 1, pos.y)
        MOVEMENT.SOUTH -> Coord(pos.x, pos.y - 1)
        MOVEMENT.WEST -> Coord(pos.x - 1, pos.y)
    }
}


fun printMap(map: Map<Coord, TILE>) {
    System.out.println("----------------------------------------")
    val xMax = map.keys.maxBy { it.x }!!.x
    val xMin = map.keys.minBy { it.x }!!.x
    val yMax = map.keys.maxBy { it.y }!!.y
    val yMin = map.keys.minBy { it.y }!!.y
    for (y in yMin..yMax) {
        for (x in xMin..xMax) {
            val tile = map.getOrDefault(Coord(x, y), TILE.UNKNOWN)
            System.out.print(tile.str)
        }
        System.out.println()
    }
    System.out.println("----------------------------------------")
}

fun findWall(map: MutableMap<Coord, TILE>, prog: Program): Coord {
    var pos = Coord(0, 0)
    var movement: MOVEMENT = MOVEMENT.NORTH

    map[pos] = TILE.START

    while (true) {
        val res = prog.execute(movement.value)

        if (res.first == STATUS.HALTED || res.first == STATUS.ERROR) {
            System.out.println(res)
            break
            //System.out.println("Calculated input: $input")
        }
        if (res.first == STATUS.RUNNING) {
            val tile = TILE.fromInt(res.second)
            val statusPos = getNextPos(pos, movement)
            map[statusPos] = tile

            when (tile) {
                TILE.WALL -> return pos
                else -> pos = statusPos
            }
        }
    }
    return pos
}

fun getWallMovements(movement: MOVEMENT): List<MOVEMENT> {
    return when (movement) {
        MOVEMENT.EAST -> listOf(MOVEMENT.NORTH, MOVEMENT.EAST, MOVEMENT.SOUTH, MOVEMENT.WEST)
        MOVEMENT.SOUTH -> listOf(MOVEMENT.EAST, MOVEMENT.SOUTH, MOVEMENT.WEST, MOVEMENT.NORTH)
        MOVEMENT.WEST -> listOf(MOVEMENT.SOUTH, MOVEMENT.WEST, MOVEMENT.NORTH, MOVEMENT.EAST)
        MOVEMENT.NORTH -> listOf(MOVEMENT.WEST, MOVEMENT.NORTH, MOVEMENT.EAST, MOVEMENT.SOUTH)
    }
}

fun followWall(map: MutableMap<Coord, TILE>, prog: Program, startPos: Coord) {
    var movement: MOVEMENT = MOVEMENT.NORTH
    var pos = startPos

    while (true) {

        val movements = getWallMovements(movement)
        for (mov in movements) {
            val res = prog.execute(mov.value)

            if (res.first == STATUS.HALTED || res.first == STATUS.ERROR) {
                System.out.println(res)
                break
                //System.out.println("Calculated input: $input")
            }
            val tile = TILE.fromInt(res.second)
            val nextPos = getNextPos(pos, mov)
            map[nextPos] = tile

            if (tile != TILE.WALL) {
                pos = nextPos
                movement = mov
                break
            }
        }

        if (pos == Coord(0, 0))
            return
    }

}

fun mapArea(map: MutableMap<Coord, TILE>, prog: Program): MutableMap<Coord, TILE> {
    // Start by finding a wall
    val pos = findWall(map, prog)
    followWall(map, prog, pos)

    return map
}

fun getNeighbors(pos: Coord): List<Coord> {
    return listOf(Coord(pos.x + 1, pos.y), Coord(pos.x - 1, pos.y), Coord(pos.x, pos.y + 1), Coord(pos.x, pos.y - 1))
}

fun bfs(map: MutableMap<Coord, TILE>, pos: Coord): Int {
    val steps = mutableMapOf<Coord, Int>()
    val queue = mutableListOf<Coord>()

    steps[pos] = 0
    queue.add(pos)

    while (queue.isNotEmpty()) {
        val position = queue.removeAt(0)
        if (map[position] == TILE.O2_SYS)
            return steps[position]!!
        for (neighbor in getNeighbors(position)) {
            if (!steps.containsKey(neighbor) && map[neighbor] != TILE.WALL) {
                steps[neighbor] = steps[position]!! + 1
                queue.add(neighbor)
            }
        }
    }
    throw Exception("Oxygen not found")
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val inputP: List<List<Int>> = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { list -> list.map { str -> str.toInt() } }

val program = mutableMapOf<Int, Int>()
inputP[0].forEachIndexed { ind, v -> program[ind] = v }
val prog = Program(program)

val map = mutableMapOf<Coord, TILE>()


// Start by mapping the entire area
mapArea(map, prog)

printMap(map)

// Find shortest path to oxygen
val steps = bfs(map, Coord(0, 0))

System.out.println("Done: $steps")
