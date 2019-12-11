#!/usr/bin/env kscript

import java.io.File

enum class DIRECTION {
    UP, DOWN, RIGHT, LEFT
}

class Program(program: MutableMap<Long, Long>) {
    private var nextInd = 0.toLong()
    private val prog = program
    private var relBase: Long = 0

    fun get(map: MutableMap<Long, Long>, ind: Long): Long {
        return map.getOrDefault(ind, 0.toLong())
    }

    fun execute(input: Int): Pair<Long, Boolean> {
        //System.out.println("Instruction: " + nextInd)
        val res = mutableListOf<Long>()
        while (prog[nextInd] != 99.toLong()) {

            val instruction = prog[nextInd]
            val instructionStr = instruction.toString().padStart(6, '0')
            val opcode = instructionStr.substring(instructionStr.length - 2, instructionStr.length).toInt()
            //System.out.println("Instruction: $instruction")

            val mode1 = instructionStr[instructionStr.length - 3]
            val mode2 = instructionStr[instructionStr.length - 4]
            val mode3 = instructionStr[instructionStr.length - 5]
            val pos1: Long = if (mode1 == '0') get(prog, nextInd + 1) else if (mode1 == '1') nextInd + 1 else relBase + get(prog, nextInd + 1)
            val pos2: Long = if (mode2 == '0') get(prog, nextInd + 2) else if (mode2 == '1') nextInd + 2 else relBase + get(prog, nextInd + 2)
            val pos3: Long = if (mode2 == '0') get(prog, nextInd + 3) else if (mode2 == '1') nextInd + 3 else relBase + get(prog, nextInd + 3)

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
                    var ind = get(prog, nextInd + 1)
                    if (mode1 == '2')
                        ind += relBase
                    prog[ind] = input.toLong()
                    nextInd += 2
                }
                4 -> {
                    //res.add(get(prog, pos1))
                    nextInd += 2
                    return Pair(get(prog, pos1), true)
                }
                5 -> {
                    if (get(prog, pos1) != 0.toLong())
                        nextInd = get(prog, pos2)
                    else
                        nextInd += 3
                }
                6 -> {
                    if (get(prog, pos1) == 0.toLong())
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
                    return Pair(-1, false)
                }
                else -> return Pair(-1, false)
            }
        }

        return Pair(-1, false)
    }
}

fun getDirection(currDir: DIRECTION, turn: Int): DIRECTION {
    when (turn) {
        0 ->
            when (currDir) {
                DIRECTION.UP -> return DIRECTION.LEFT
                DIRECTION.LEFT -> return DIRECTION.DOWN
                DIRECTION.DOWN -> return DIRECTION.RIGHT
                DIRECTION.RIGHT -> return DIRECTION.UP
            }
        1 ->
            when (currDir) {
                DIRECTION.UP -> return DIRECTION.RIGHT
                DIRECTION.LEFT -> return DIRECTION.UP
                DIRECTION.DOWN -> return DIRECTION.LEFT
                DIRECTION.RIGHT -> return DIRECTION.DOWN
            }
    }
    return DIRECTION.UP
}

fun getNextPos(pos: Pair<Int, Int>, dir: DIRECTION): Pair<Int, Int> {
    when (dir) {
        DIRECTION.UP -> return Pair(pos.first, pos.second + 1)
        DIRECTION.LEFT -> return Pair(pos.first - 1, pos.second)
        DIRECTION.DOWN -> return Pair(pos.first, pos.second - 1)
        DIRECTION.RIGHT -> return Pair(pos.first + 1, pos.second)
    }
}


if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val inputP: List<List<Long>> = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { list -> list.map { str -> str.toLong() } }

val program = mutableMapOf<Long, Long>()
inputP[0].forEachIndexed { ind, v -> program[ind.toLong()] = v }
val prog = Program(program)

var executing = true
val map = mutableMapOf<Pair<Int, Int>, Int>()
var pos = Pair(0, 0)
var direction = DIRECTION.UP
map[pos] = 1

while (executing) {
    val currColor = map.getOrDefault(pos, 0)
    val paintColour = prog.execute(currColor)
    val nextDirection = prog.execute(currColor)
    map[pos] = paintColour.first.toInt()

    executing = paintColour.second && nextDirection.second
    direction = getDirection(direction, nextDirection.first.toInt())
    pos = getNextPos(pos, direction)
}

val minX: Int = map.entries.minBy { it.key.first }!!.key.first
val maxX: Int = map.entries.maxBy { it.key.first }!!.key.first
val minY: Int = map.entries.minBy { it.key.second }!!.key.second
val maxY: Int = map.entries.maxBy { it.key.second }!!.key.second


for (y in minY..maxY) {
    for (x in minX..maxX) {
        val colour = map.getOrDefault(Pair(x, y), 0)
        if (colour == 0)
            System.out.print(" ")
        else
            System.out.print("O")

    }
    System.out.println()
}

System.out.println("Result: " + map.size)
