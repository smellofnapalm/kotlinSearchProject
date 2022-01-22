package search

import java.io.File

enum class Strategy {
    ALL,
    ANY,
    NONE
}

fun printMenu(menu: Array<String>) {
    println("=== Menu ===")
    for (i in menu.indices)
        println("${i+1}. ${menu[i]}")
    println("0. Exit")
}

fun makeInvertedIndex(dataBase: List<String>) : MutableMap<String, MutableSet<Int>> {
    var invertedIndex = mutableMapOf<String, MutableSet<Int>>()
    for (line in dataBase) {
        val words = line.split(" ")
        for (word in words) {
            if (invertedIndex[word.lowercase().trim()] == null)
                invertedIndex[word.lowercase().trim()] = mutableSetOf<Int>()
            for (i in dataBase.indices) {
                if (word.lowercase().trim() in dataBase[i].split(" ").map { it.lowercase().trim() })
                    invertedIndex[word.lowercase().trim()]!!.add(i)
            }
        }
    }
    return invertedIndex
}

fun findPerson(pattern: String, dataBase: List<String>, invertedIndex: MutableMap<String, MutableSet<Int>>, strategy: Strategy) {
    val words = pattern.lowercase().split(" ")
    for (word in words)
        if (invertedIndex[word] == null)
            invertedIndex[word] = mutableSetOf<Int>()
    var findings = mutableSetOf<Int>()
    if (strategy == Strategy.ALL) {
        findings = invertedIndex[words.first()]!!
        for (word in words)
            findings = findings.intersect(invertedIndex[word]!!).toMutableSet()
    }
    else {
        for (word in words)
            findings = findings.union(invertedIndex[word]!!).toMutableSet()
        if (strategy == Strategy.NONE)
            findings = dataBase.indices.minus(findings).toMutableSet()
    }

    if (findings.isEmpty())
        println("No matching people found.")
    else {
        println("${findings!!.size} ${if (findings!!.size >= 2) "persons" else "person"} found:")
        for (i in findings)
            println(dataBase[i])
    }
}

fun main(args: Array<String>) {
    val menu: Array<String> = arrayOf("Find a person", "Print all people")
    val dataBase = File("src\\main\\kotlin\\${args[1]}").readLines()
    val n = dataBase.size
    var invertedIndex = makeInvertedIndex(dataBase)

    while(true) {
        printMenu(menu)
        when(readLine()!!.toIntOrNull()) {
            0 -> {
                println("Bye!")
                break
            }
            1 -> {
                println("Select a matching strategy: ALL, ANY, NONE")
                val strategy = Strategy.valueOf(readLine()!!)
                println("Enter a name or email to search all suitable people.")
                val pattern = readLine()!!
                findPerson(pattern, dataBase, invertedIndex, strategy)
            }
            2 -> {
                println("=== List of people ===")
                for (person in dataBase)
                    println(person)
            }
            else -> println("Incorrect option! Try again.")
        }
    }
}