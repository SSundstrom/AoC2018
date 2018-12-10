package days

import input.*

fun day1(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    return "Pt1: " + getInput(1).lines().fold(0, {sum, i -> sum + i.toInt()}).toString()
}

private fun pt2(): String {
    val input = getInput(1).lines().map { it.toInt() }
    var sum = 0
    val seen = mutableSetOf<Int>()
    var iterator = input.listIterator()
    while (seen.add(sum)) {
        if (!iterator.hasNext()) {iterator = input.listIterator()}
        sum += iterator.next()
    }
    return "Pt2: " + sum.toString()
}