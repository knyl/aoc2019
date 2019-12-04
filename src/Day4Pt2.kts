#!/usr/bin/env kscript

fun countIncreasing(low: Int, high: Int): Int {
    var count = 0
    for (i in low..high) {
        if (isIncreasing(i))
            count++
    }
    return count
}

fun isIncreasing(num: Int): Boolean {
    val numAsString = num.toString()
    var ordered = true
    for (i in 0..(numAsString.length-2)) {
        if (numAsString[i] > numAsString[i+1]) {
            ordered = false
        }
    }

    val matches = Regex("(\\w)\\1+").findAll(numAsString)
    val subGroupCountTwo = matches.any { it.value.length == 2 }

    return ordered && subGroupCountTwo
}

val low = 264793
val high = 803935

val result = countIncreasing(low, high)

System.out.println("Result: " + result)

