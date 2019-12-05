#!/usr/bin/env kscript

import java.io.File

fun executeProgram(program: List<Int>): MutableList<Int> {
    val input = 5
    var nextInd = 0
    val prog = program.toMutableList()
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
                prog[prog[nextInd + 1]] = input
                nextInd += 2
            }
            4 -> {
                System.out.println(prog[pos1])
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
    return prog
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val results = File(fileName).readLines()
        .map { line -> line.split(',') }
        .map { list -> list.map { str -> str.toInt() } }
        .map { program -> executeProgram(program) }


results.forEach { result -> System.out.println("Result: $result") }

