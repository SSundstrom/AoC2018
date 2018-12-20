package days
import input.*

fun day12(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(40) + pt2()
}

private fun pt1() : String {
    val gens = 20.toLong()
    val plantSet = doThings(gens)
    return "Pt1: " + plantSet.toString()
}

private fun pt2() : String {
    val gens = 50000000000
    val plantSet = doThings(gens)
    return "Pt2: " + plantSet.toString()
    // Expected 2600000001872 saser input
}

private val primArray = intArrayOf(1, 2, 4, 8, 16)

private fun doThings(gens: Long): Long {
    val inData = getInput(12).lines()
    val initPlants = inData[0].dropWhile { it != '#' }.map { it == '#' }.dropLastWhile { !it }
    var firstTrue = 0.toLong()
    val plantPattern = BooleanArray(32)
    inData.drop(2).forEach {
        if (it.last() == '#') {
            var sum = 0
            for (i in 0..4) {
                when (it[i]) {
                    '#' -> sum += primArray[i]
                }
            }
            plantPattern[sum] = true
        }
    }

    val prevGens = mutableSetOf<PlantIter>()
    var firstOccurrence = PlantIter(initPlants, 0, 0)
    var currentGen = firstOccurrence
    for (x in 1 .. gens) {
        var newPlants = mutableListOf<Boolean>()

        for (i in -2 until currentGen.plants.size + 2) {
            val plant = plantPattern[getPatternFrom(i, currentGen.plants)]
            newPlants.add(plant)
        }

        val newFirstTrue = newPlants.indexOfFirst { it }
        val newLastTrue = newPlants.indexOfLast { it }
        newPlants = newPlants.subList(newFirstTrue, newLastTrue + 1)
        firstTrue += newFirstTrue - 2

        val newPlantGen = PlantIter(newPlants, x, firstTrue)
        currentGen = newPlantGen

        if (newPlantGen in prevGens) {
            firstOccurrence = prevGens.find { it == newPlantGen }?:firstOccurrence
            break
        } else {
            prevGens.add(newPlantGen)
        }
    }

    val remainingGenerations = (gens - currentGen.gen)
    val loops = remainingGenerations / (currentGen.gen - firstOccurrence.gen)
    var firstPlant = currentGen.firstPlant
    firstPlant += (firstPlant - firstOccurrence.firstPlant) * loops
    var sum = 0.toLong()
    currentGen.plants.forEachIndexed { index, b ->
        if (b) { sum += index + firstPlant }
    }

    return sum
}


private val padding = listOf(false, false, false, false)

private fun getPatternFrom(index : Int, prevPlants : List<Boolean>): Int {
    var retVal = 0
    val bools = mutableListOf<Boolean>()
    bools.addAll(padding); bools.addAll(prevPlants); bools.addAll(padding)
    for (i in -2 .. 2) {
        retVal += if (bools[i+index+4]) { primArray[i+2] } else { 0 }
    }
    return retVal
}

private class PlantIter constructor(val plants : List<Boolean>, val gen : Long, val firstPlant: Long) {

    override fun hashCode(): Int {
        return plants.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PlantIter
        return plants == other.plants
    }
}

private fun printLine(lst : List<Boolean>) {
    val str = lst.map { if (it) {'#'} else {'.'} }.joinToString("")
    println(str)
}
