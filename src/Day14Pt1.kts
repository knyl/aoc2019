#!/usr/bin/env kscript

import java.io.File

fun solve2(reactants: Map<String, List<Pair<String, Int>>>, productNum: Map<String, Int>, available: MutableMap<String, Int>, reactant: String, requested: Int): Int {
    val availableProd = available.getOrDefault(reactant, 0)
    var createdProd: Int
    if (requested > availableProd) {
        createdProd = create(availableProd, requested, productNum.getValue(reactant))
        val excess = createdProd + availableProd - requested
        available[reactant] = excess
    } else {
        createdProd = 0
        val excess = availableProd - requested
        available[reactant] = excess
    }
    var acc = 0
    if (createdProd > 0) {
        for (r in reactants.getOrDefault(reactant, emptyList())) {
            val foo = createdProd / productNum.getValue(reactant) * r.second
            if (r.first == "ORE") {
                acc += foo
            } else {
                acc += solve2(reactants, productNum, available, r.first, foo)
            }
        }
    }
    return acc
}

fun create(available: Int, requested: Int, reactionResult: Int): Int {
    val toProduce = requested - available
    return when {
        toProduce <= reactionResult -> reactionResult
        toProduce % reactionResult == 0 -> toProduce
        else -> (toProduce / reactionResult + 1) * reactionResult
    }
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val lines: List<String> = File(fileName).readLines()
val reactants = mutableMapOf<String, List<Pair<String, Int>>>()
val productNum = mutableMapOf<String, Int>()

lines.forEach { s ->
    val (lhs, rhs) = s.split("=>".toRegex(), 2)
    val (reactantNum: String, reactant: String) = rhs.trim().split(" ")
    val lhsSplit = lhs.split(",").map {
        val (l: String, r: String) = it.trim().split(" ")
        Pair(r, l.toInt())
    }
    reactants[reactant] =  lhsSplit
    productNum[reactant] = reactantNum.toInt()
}

val results = solve2(reactants.toMap(), productNum.toMap(), mutableMapOf(), "FUEL", 1)

System.out.println("Result: $results")

