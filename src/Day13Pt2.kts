#!/usr/bin/env kscript

import java.io.File
import kotlin.text.StringBuilder

enum class STATUS {
    RUNNING, WAITING_FOR_INPUT, HALTED, ERROR
}

class Program(program: MutableMap<Long, Long>) {
    private var nextInd = 0.toLong()
    private val prog = program
    private var relBase: Long = 0

    fun get(map: MutableMap<Long, Long>, ind: Long): Long {
        return map.getOrDefault(ind, 0.toLong())
    }

    fun execute(): Pair<STATUS, Long> {
        return execute(null)
    }

    fun execute(input: Long?): Pair<STATUS, Long> {
        //System.out.println("Instruction: " + nextInd)
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
                    prog[ind] = input.toLong()
                    nextInd += 2
                }
                4 -> {
                    //res.add(get(prog, pos1))
                    //System.out.println("Output: " + get(prog, pos1))
                    nextInd += 2
                    return Pair(STATUS.RUNNING, get(prog, pos1))
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
                    return Pair(STATUS.HALTED, 0)
                }

                else -> return Pair(STATUS.ERROR, 0)
            }
        }

        return Pair(STATUS.HALTED, 0)
    }
}

fun calculateMove(map: Map<Pair<Long, Long>, Long>): Long {
    val ball = map.filter { it.value == 4.toLong() }.keys.firstOrNull()
    val paddle = map.filter { it.value == 3.toLong() }.keys.firstOrNull()
    if (paddle != null && ball != null) {
        if (paddle.first < ball.first)
            return 1
        else if (paddle.first == ball.first)
            return 0
        else
            return -1
    }
    printMap(map)
    System.out.println("Error move?")
    return 0
}

fun getTile(tileLong: Long): Char {
    val tile = tileLong.toInt()
    return when (tile) {
        0 -> ' '
        1 -> '|'
        2 -> 'x'
        3 -> '_'
        4 -> 'o'
        else -> throw Exception("Error!")
    }
}

fun printMap(map: Map<Pair<Long, Long>, Long>) {
    val xMax = map.keys.maxBy { it.first }!!.first
    val yMax = map.keys.maxBy { it.second }!!.second
    val stringBuilder = StringBuilder()
    for (y in 0..yMax) {
        for (x in 0..xMax) {
            val tile = getTile(map.getOrDefault(Pair(x, y), 0.toLong()))
            stringBuilder.append(tile)
        }
        stringBuilder.append("\n")
    }
    System.out.println(stringBuilder.toString())
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
program[0.toLong()] = 2
val prog = Program(program)

var executing = true
val map = mutableMapOf<Pair<Long, Long>, Long>()

var x = 0.toLong()
var y = 0.toLong()
var tile = 0.toLong()
var input: Long? = null


while (executing) {
    var res = prog.execute(input)
    input = null
    if (res.first == STATUS.WAITING_FOR_INPUT) {
        input = calculateMove(map)
        //System.out.println("Calculated input: $input")
    }
    if (res.first == STATUS.RUNNING) {
        x = res.second
        res = prog.execute()
        y = res.second
        res = prog.execute()
        tile = res.second

        if (x == (-1).toLong() && y == 0.toLong())
            System.out.println("Score: $tile")
        else {
            //System.out.println("Storing ($x, $y): $tile")
            map[Pair(x, y)] = tile
        }
    }
    if (res.first == STATUS.ERROR || res.first == STATUS.HALTED) {
        System.out.println(res.first)
        executing = false
        break
    }

    printMap(map)
}

//System.out.println("Result: " + result)
