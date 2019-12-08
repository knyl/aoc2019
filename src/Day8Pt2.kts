#!/usr/bin/env kscript

import java.io.File

if (args.size != 1) {
    System.out.println("Need to provide an input file!")
    System.exit(1)
}
val fileName = args.toList().get(0)

val imageSize = 25 * 6

val input: List<String> = File(fileName).readLines()

val data = input[0]

val result = data.chunked(imageSize)
var image = mutableListOf<Char>()

for (i in 0..imageSize-1) {
    for (j in result.indices) {
        if (result[j][i] != '2') {
            image.add(result[j][i])
            break
        }
    }
}

var pos = 0;
for (i in 0..5) {
    for (j in 0..24) {
        if (image[pos] == '1')
          System.out.print(image[pos])
        else
            System.out.print(" ")
        pos++
    }
    System.out.println()
}
//System.out.println("Result: " + result.second)

