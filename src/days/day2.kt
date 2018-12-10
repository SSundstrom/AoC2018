package days
import input.*

fun day2(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val pair = getInput(2)
            .lines()
            .map { line -> line
                    .map { ch -> line
                            .count( ch::equals) }
                    .toSet() }
            .fold(Pair(0, 0), {curr , sum ->
                Pair(curr.first + sum.count { x -> 2==x }, curr.second + sum.count { x -> 3==x })})
    return "Pt1: " + (pair.first * pair.second).toString()
}

private fun pt2() : String {
    val strings = getInput(2).lines().sorted()
    val iter = strings.listIterator()
    var res : String?
    var oldLine = iter.next()
    var newLine = iter.next()
    do {
        res = oldLine.comp(newLine)
        oldLine = newLine; newLine = iter.next()
    } while (res.isNullOrBlank())
    return "Pt2: " + res
}

private fun String.comp(other : String) : String? {
    val retWord = mutableListOf<Char>()
    var diff = 0
    (0..(this.length -1)).forEach {
        if (this[it] == other[it]) {
            retWord.add(this[it])
        } else {
            if (diff > 0) {
                return null
            } else {
                diff++
            }
        }
    }
    return retWord.joinToString("")
}