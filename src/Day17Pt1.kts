#!/usr/bin/env kscript

import java.io.File

data class Coord(val x: Int, val y: Int)

enum class TILE(val value: Int, val str: Char) {
    WALL(0, '#'), OPEN(1, '.'), START(3, 'x'), O2_SYS(2, 'o'), UNKNOWN(4, ' ');

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

enum class TURN {
    LEFT, RIGHT
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
        var inputVal = input
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
                    if (inputVal == null) {
                        //System.out.println("Waiting for input")
                        return Pair(STATUS.WAITING_FOR_INPUT, 0)
                    } else {
                        //System.out.println("Got input: " + inputVal)
                    }
                    var ind = get(prog, nextInd + 1)
                    if (mode1 == '2')
                        ind += relBase
                    prog[ind] = inputVal
                    inputVal = null
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
    val xMax = map.keys.maxBy { it.x }!!.x
    val xMin = map.keys.minBy { it.x }!!.x
    val yMax = map.keys.maxBy { it.y }!!.y
    val yMin = map.keys.minBy { it.y }!!.y
    val stringBuilder = StringBuilder()
    for (y in yMin..yMax) {
        for (x in xMin..xMax) {
            val tile = map.getOrDefault(Coord(x, y), TILE.UNKNOWN)
            stringBuilder.append(tile.str)
        }
        stringBuilder.append("\n")
    }
    System.out.println(stringBuilder.toString())
}

fun getNeighbors(pos: Coord): List<Coord> {
    return listOf(Coord(pos.x + 1, pos.y), Coord(pos.x - 1, pos.y), Coord(pos.x, pos.y + 1), Coord(pos.x, pos.y - 1))
}

fun isCrossing(pos: Coord, map: Map<Coord, TILE>): Boolean {
    return getNeighbors(pos).all { map.containsKey(it) }
}

fun getTurn(dir1: MOVEMENT, dir2: MOVEMENT): TURN {
    return when {
        dir1 == MOVEMENT.EAST && dir2 == MOVEMENT.NORTH -> TURN.LEFT
        dir1 == MOVEMENT.EAST && dir2 == MOVEMENT.SOUTH -> TURN.RIGHT
        dir1 == MOVEMENT.WEST && dir2 == MOVEMENT.NORTH -> TURN.RIGHT
        dir1 == MOVEMENT.WEST && dir2 == MOVEMENT.SOUTH -> TURN.LEFT
        dir1 == MOVEMENT.NORTH && dir2 == MOVEMENT.EAST -> TURN.RIGHT
        dir1 == MOVEMENT.NORTH && dir2 == MOVEMENT.WEST -> TURN.LEFT
        dir1 == MOVEMENT.SOUTH && dir2 == MOVEMENT.EAST -> TURN.LEFT
        dir1 == MOVEMENT.SOUTH && dir2 == MOVEMENT.WEST -> TURN.RIGHT
        else -> throw Exception("Unexpected movement: $dir1 $dir2")
    }
}

fun getDirection(pos1: Coord, pos2: Coord): MOVEMENT {
    return when {
        pos1.x < pos2.x -> MOVEMENT.EAST
        pos1.x > pos2.x -> MOVEMENT.WEST
        pos1.y < pos2.y -> MOVEMENT.SOUTH
        pos1.y > pos2.y -> MOVEMENT.NORTH
        else -> throw Exception("Unexpected direction! $pos1 $pos2")
    }
}

fun nextPosition(map: Map<Coord, TILE>, pos: Coord, direction: MOVEMENT): Coord? {
    val nextPos = when (direction) {
        MOVEMENT.NORTH -> Coord(pos.x, pos.y - 1)
        MOVEMENT.SOUTH -> Coord(pos.x, pos.y + 1)
        MOVEMENT.WEST -> Coord(pos.x - 1, pos.y)
        MOVEMENT.EAST -> Coord(pos.x + 1, pos.y)
    }
    return if (map.containsKey(nextPos)) nextPos else null
}

/*
fun walkMap(map: Map<Coord, TILE>, start: Coord, startDir: MOVEMENT): String {
    val firstNeighbor = getNeighbors(start).filter { map.containsKey(it) }.first()
    var direction = getDirection(start, firstNeighbor)
    val firstTurn = getTurn(startDir, direction)
    var currentPosition = firstNeighbor
    while (true) {
        val nextNeighbor = nextPosition(map, currentPosition, direction);
        if (nextNeighbor == null) {  // turn!
            val turnLeft = turnLeft(map, currentPosition, direction)

        }
    }

    return ""
}
 */

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
var x = 0
var y = 0
var robot = Coord(0, 0)
var robotChar = 'c'
/*

while (true) {
    val res = prog.execute()

    if (res.first == STATUS.HALTED || res.first == STATUS.ERROR) {
        break
    }
    val output = res.second
    when (output) {
        10 -> { // newline
            x = 0
            y++
        }
        35 -> { // path
            map[Coord(x, y)] = TILE.WALL
            x++
        }
        46 -> { // Nothing
            x++
        }
        else -> {
            map[Coord(x, y)] = TILE.WALL
            robot = Coord(x, y)
            robotChar = output.toChar()
            x++
        }
    }
}

val direction = when (robotChar) {
    '^' -> MOVEMENT.NORTH
    '>' -> MOVEMENT.EAST
    '<' -> MOVEMENT.WEST
    'v' -> MOVEMENT.SOUTH
    else -> throw Exception("Unrecognized mvoement: $robotChar")
}

//val path = walkMap(map, robot, direction)

printMap(map)

val patternA = "R12L10L10"
val patternB = "L6L12R12L4"
val patternC = "L12R12L6"
val path = "ABABCBCABB"

val result = map.keys.filter { isCrossing(it, map) }.map { it.x * it.y }.sum()

System.out.println("Robot: $robot $robotChar")
System.out.println("Part 1: $result")

 */

val input = "A,B,A,B,C,B,C,A,C,C\nR,12,L,10,L,10\nL,6,L,12,R,12,L,4\nL,12,R,12,L,6\nn\n"
val program2 = mutableMapOf<Int, Int>()
inputP[0].forEachIndexed { ind, v -> program2[ind] = v }
program2[0] = 2
val prog2 = Program(program)
var res: Pair<STATUS, Int>

var inputInd = 0

var nextInput: Int? = null

while (true) {
    if (nextInput == null) {
        //System.out.println("Not input")
        res = prog2.execute()
    }
    else {
        //System.out.println("executing input")
        res = prog2.execute(nextInput)
        nextInput = null
    }

    if (res.first == STATUS.HALTED || res.first == STATUS.ERROR) {
        System.out.println(res.first)
        break
    }
    if (res.first == STATUS.RUNNING) {
        if (res.second > 10000)
            System.out.println(res.second)
        else
            System.out.print(res.second.toChar())
    }
    if (res.first == STATUS.WAITING_FOR_INPUT) {
        System.out.print(input[inputInd])
        nextInput = input[inputInd].toInt()
        inputInd++
        System.out.println() // newline..
    }
}
