package days
import input.*

fun day5(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val indata = getInput(5).toMutableList()
    while (indata.removeCollisions()) {}
    return "Pt1: " + indata.size.toString()
}

private fun pt2() : String {
    val data2 = getInput(5).toMutableList()

    val reductions = ('a'..'z').map { letter : Char ->
        val filtered = data2.filter { x -> x.toLowerCase() != letter }.toMutableList()
        while (filtered.removeCollisions()) {}
        return@map Pair(letter, filtered.size)
    }
    return reductions.minBy { it.second }?.second.toString()
}

fun MutableList<Char>.removeCollisions() : Boolean {
    val iter = this.listIterator()
    var anyRemoved = false
    while (iter.hasNext()) {
        val a = iter.next().changeCase()
        if (!iter.hasNext()) { return anyRemoved }
        val b = iter.next()
        if (a == b) {
            iter.remove()
            iter.previous()
            iter.remove()
            anyRemoved = true
        }
        if (iter.hasPrevious()) {iter.previous()}
    }
    return anyRemoved
}

fun Char.changeCase(): Char {
    return if (this.isUpperCase()) { this.toLowerCase() }
            else { this.toUpperCase() }
}