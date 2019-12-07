#!/usr/bin/env kscript

import java.io.File

fun executeProgram(program: List<Int>, input: Int, phaseSetting: Int): Int {
    var firstInput = true
    var nextInd = 0
    val prog = program.toMutableList()
    var output = 0
    while (prog[nextInd] != 99) {

        val instruction = prog[nextInd]
        val instructionStr = instruction.toString().padStart(6, '0')
        val opcode = instructionStr.substring(instructionStr.length - 2, instructionStr.length).toInt()

        val mode1 = instructionStr[instructionStr.length - 3]
        val mode2 = instructionStr[instructionStr.length - 4]
        val pos1 = if (mode1 == '0') prog[nextInd + 1] else nextInd + 1
        val pos2 = if (mode2 == '0') prog[nextInd + 2] else nextInd + 2

        when (opcode) {
            1 -> {
                prog[prog[nextInd + 3]] = prog[pos1] + prog[pos2]
                nextInd += 4
            }
            2 -> {
                prog[prog[nextInd + 3]] = prog[pos1] * prog[pos2]
                nextInd += 4
            }
            3 -> {
                if (firstInput) {
                    prog[prog[nextInd + 1]] = phaseSetting
                    firstInput = false
                } else {
                    prog[prog[nextInd + 1]] = input
                }
                nextInd += 2
            }
            4 -> {
                output = prog[pos1]
                nextInd += 2
            }
            5 -> {
                if (prog[pos1] != 0)
                    nextInd = prog[pos2]
                else
                    nextInd += 3
            }
            6 -> {
                if (prog[pos1] == 0)
                    nextInd = prog[pos2]
                else
                    nextInd += 3
            }
            7 -> {
                if (prog[pos1] < prog[pos2])
                    prog[prog[nextInd+3]] = 1
                else
                    prog[prog[nextInd+3]] = 0
                nextInd += 4
            }
            8 -> {
                if (prog[pos1] == prog[pos2])
                    prog[prog[nextInd+3]] = 1
                else
                    prog[prog[nextInd+3]] = 0
                nextInd += 4
            }
        }
    }
    return output
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
            i+=1
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

val programs: List<List<Int>> = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { list -> list.map { str -> str.toInt() } }
        //.map { program -> executeProgram(program) }

val program = programs[0]

val nums = mutableListOf(0, 1, 2, 3, 4)

val permutations = permutations(5, nums)

var results = mutableListOf<Int>()
for (i in permutations.indices) {
    val permutation = permutations[i]
    val res = permutation.fold(0) { acc, it ->
        executeProgram(program, acc, it)
    }
    results.add(res)
}

System.out.println("Result: " + results.max())
