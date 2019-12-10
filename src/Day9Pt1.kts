#!/usr/bin/env kscript

import java.io.File

class Program(program: MutableMap<Long, Long>) {
    private var nextInd = 0.toLong()
    private val prog = program

    fun get(map: MutableMap<Long, Long>, ind: Long): Long {
        return map.getOrDefault(ind, 0.toLong())
    }
    fun execute(input: Int): Pair<Int, Boolean> {
        //System.out.println("Instruction: " + nextInd)
        val res = mutableListOf<Long>()
        var relBase: Long = 0
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
                    var ind = get(prog, nextInd + 1)
                    if (mode1 == '2')
                        ind += relBase
                    prog[ind] = input.toLong()
                    nextInd += 2
                }
                4 -> {
                    res.add(get(prog, pos1))
                    nextInd += 2
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
                    if (get(prog,pos1) < get(prog,pos2))
                        prog[ind] = 1
                    else
                        prog[ind] = 0
                    nextInd += 4
                }
                8 -> {
                    var ind = get(prog, nextInd + 3)
                    if (mode3 == '2')
                        ind += relBase
                    if (get(prog,pos1) == get(prog,pos2))
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
                    System.out.println("Res. " + res)
                    return Pair(-1, false)
                }
            }
            if (instruction!!.equals("203"))
                return Pair(-1, false)
        }

        System.out.println("Res. " + res)
        return Pair(-1, false)
    }
}

fun permutations(n: Int, A: MutableList<Int>): List<List<Int>> {
    val permutations = mutableListOf<List<Int>>()
    val c = mutableListOf<Int>()
    for (i in 0..n) {
        c.add(0)
    }

    // output A
    permutations.add(A.toList())
    var i = 0

    while (i < n) {
        if (c[i] < i) {
            if (i % 2 == 0) {
                swap(A, 0, i)
            } else {
                swap(A, c[i], i)
            }
            // output A
            permutations.add(A.toList())
            c[i] += 1
            i = 0
        } else {
            c[i] = 0
            i += 1
        }
    }

    return permutations
}

fun swap(input: MutableList<Int>, a: Int, b: Int) {
    val tmp: Int = input[a]
    input[a] = input[b]
    input[b] = tmp
}


if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val inputP: List<List<Long>> = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { list -> list.map { str -> str.toLong() } }
//.map { program -> executeProgram(program) }


val program = mutableMapOf<Long, Long>()

inputP[0].forEachIndexed { ind, v -> program[ind.toLong()] = v }

val input = 2
val prog = Program(program)
val result = prog.execute(input)

System.out.println("Result: " + result)
