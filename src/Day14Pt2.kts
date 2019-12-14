#!/usr/bin/env kscript

import java.io.File
import kotlin.math.ceil

fun solve2(reactants: Map<String, List<Pair<String, Long>>>, productNum: Map<String, Long>, available: MutableMap<String, Long>, reactant: String, requested: Long): Long {
    // TODO: stop clause!!!
    val availableProd = available.getOrDefault(reactant, 0.toLong())
    var createdProd: Long
    if (requested > availableProd) {
        createdProd = create(availableProd, requested, productNum.getValue(reactant))
        val excess = createdProd + availableProd - requested
        available[reactant] = excess
    } else {
        createdProd = 0.toLong()
        val excess = availableProd - requested
        available[reactant] = excess
    }
    var acc = 0.toLong()
    if (createdProd > 0.toLong()) {
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

fun create(available: Long, requested: Long, reactionResult: Long): Long {
    val toProduce = requested - available
    return when {
        toProduce <= reactionResult -> reactionResult
        toProduce % reactionResult == 0.toLong() -> toProduce
        else -> (toProduce / reactionResult + 1) * reactionResult
    }
}

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val lines: List<String> = File(fileName).readLines()
val reactants = mutableMapOf<String, List<Pair<String, Long>>>()
val productNum = mutableMapOf<String, Long>()

lines.forEach { s ->
    val (lhs, rhs) = s.split("=>".toRegex(), 2)
    val (reactantNum: String, reactant: String) = rhs.trim().split(" ")
    val lhsSplit = lhs.split(",").map {
        val (l: String, r: String) = it.trim().split(" ")
        Pair(r, l.toLong())
    }
    reactants[reactant] =  lhsSplit
    productNum[reactant] = reactantNum.toLong()
}
var low = 378929.toLong()
var high: Long = 26390160

val ore = 1000000000000

while (low <= high) {
    val next = ceil((low + high).toDouble() / 2).toLong()
    val results = solve2(reactants.toMap(), productNum.toMap(), mutableMapOf(), "FUEL", next)
    if (results < ore) {
        low = next + 1
    } else if (results > ore){
        high = next - 1
    } else {
        break
    }
}

System.out.println("Result: $high")

