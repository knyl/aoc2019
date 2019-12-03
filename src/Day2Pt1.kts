#!/usr/bin/env kscript

import java.io.File

fun executeProgram(program: List<Int>): MutableList<Int> {
    var nextInd = 0
    val prog = program.toMutableList()
    prog[1] = 12
    prog[2] = 2
    while (prog[nextInd] != 99) {
        readInstruction(nextInd, prog)
        nextInd = nextInd + 4
    }
    return prog
}

fun readInstruction(startInd: Int, program: MutableList<Int>): MutableList<Int> {
    val instruction = program[startInd]
    when (instruction) {
        1 -> return doAddition(startInd, program)
        2 -> return doMultiplication(startInd, program)
        99 -> return mutableListOf()
    }
    return mutableListOf()
}

fun doAddition(startInd: Int, program: MutableList<Int>): MutableList<Int> {
    program[program[startInd + 3]] = program[program[startInd + 1]] + program[program[startInd + 2]]
    return program
}

fun doMultiplication(startInd: Int, program: MutableList<Int>): MutableList<Int> {
    program[program[startInd + 3]] = program[program[startInd + 1]] * program[program[startInd + 2]]
    return program
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

