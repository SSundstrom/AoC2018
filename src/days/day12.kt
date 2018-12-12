package days
import input.*
import kotlin.system.measureTimeMillis

fun day12(pt: Int): String {
    when (1) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val gens = 20000000.toLong()
    val plantSet = doThings(gens)
    return "Pt1: " + plantSet.toString()
}

private fun pt2() : String {
    val gens = 50000000000
    val plantSet = doThings(gens)
    return "Pt1: " + plantSet.toString()
}

val primArray = intArrayOf(1, 2, 4, 8, 16)

private fun doThings(gens: Long): Int {
    val indata = getInput(12).lines()
    var plantSet = indata[0].dropWhile { it != '#' }.map { it == '#' }.toBooleanArray()
    var offset = 0
    val plantPattern = BooleanArray(32)
    indata.drop(2).forEach {
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

    var patternT = 0.toLong()
    var isInT = 0.toLong()
    var arrT = 0.toLong()
    for (x in 1 .. gens) {
        val arrOff = getNewArr(plantSet, offset)
        arrT +=  measureTimeMillis { getNewArr(plantSet, offset) }
        val newArr = arrOff.first
        val newOff = arrOff.second
        for (i in 0 until newArr.size) {
            var pattern = getPatternFrom(i-newOff+offset, plantSet)
            patternT += measureTimeMillis { pattern = getPatternFrom(i-newOff+offset, plantSet) }
            isInT += measureTimeMillis {
            newArr[i] = plantPattern[pattern]
            }
        }
        plantSet = newArr
        offset = newOff
    }
    var sum = 0
    for (i in 0 until plantSet.size) {
        if (plantSet[i]) { sum += i - offset }
    }
    println("Time: \n$patternT pattern\n$isInT isIn\n$arrT arrT\nsize = ${plantSet.size}\n")
    return sum
}
private fun getPatternFrom(index : Int, plantSet : BooleanArray): Int {
    var retVal = 0
    for (i in -2..2) {
        retVal += when (i+index) {
            -1 -> 0
            -2 -> 0
            -3 -> 0
            -4 -> 0
            plantSet.size -> 0
            plantSet.size + 1 -> 0
            plantSet.size + 2 -> 0
            plantSet.size + 3 -> 0
            else -> if (plantSet[i+index]) {primArray[i+2]} else {0}
        }
                //if (index + i < 0 || index + i >= plantSet.size) {'.'} else {plantSet[index + i]}
    }
    return retVal
}

private fun printPlantSet(plantSet: MutableSet<Int>): Unit {
    val ret = mutableListOf<Char>()
    for (i in (plantSet.min()?:0) .. (plantSet.max()?:0)) {
        if (i in plantSet) {ret.add('#')} else {ret.add('.')}
    }
    println(ret.joinToString(""))
}

fun getNewArr(prevArr : BooleanArray, offset : Int) : Pair<BooleanArray, Int> {
    var s = 0
    var newOffset = offset
    while (!prevArr[s]) {
        s++
        newOffset--
    }
    var e = prevArr.size-1
    while (!prevArr[e]) {
        e--
    }
    return Pair(BooleanArray(e-s+4), newOffset+2)
}