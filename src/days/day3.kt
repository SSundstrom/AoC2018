package days
import input.*
import util.*

fun day3(pt: Int): String {
    val sheets = formatInput()
    when (pt) {
        1 -> return pt1(sheets)
        2 -> return pt2(sheets)
    }
    return pt1(sheets) + "\t" + pt2(sheets)
}

private fun pt1(sheets : List<Sheet>) : String {
    val posMap = mutableMapOf<Pos, Int>()
    sheets.forEach {
        (0..it.width).forEach { x ->
            (0..it.height).forEach { y ->
                posMap.getOrPut(it.pos.move(x, y), {0}).inc()
            }
        }
    }
    return "Pt1: " + posMap.filter { it.value > 1 }.count().toString()
}

private fun pt2(sheets : List<Sheet>) : String {
    val posMap = mutableMapOf<Pos, Int>()
    val availablilityMap = sheets.map {
        var dup = false
        (0..it.width).forEach { x ->
            (0..it.height).forEach { y ->
                val a = posMap.getOrPut(it.pos.move(x, y), {0}).inc()
                if (a > 1) { dup = true }
            }
        }
        return@map Pair(it.id, dup)
    }.toMap()

    return "Pt2: " + availablilityMap.filter { it.value }.keys
}

private class Sheet constructor(val id : Int,
                                val pos : Pos,
                                val width : Int,
                                val height : Int)

private fun formatInput(): List<Sheet> {
    return getInput(3)
            .lines()
            .map { Regex("[0-9]+")
                    .findAll(it)
                    .map { it.value.toInt() }
                    .toList() }
            .map { Sheet(it[0], Pos(it[1], it[2]), it[3], it[4]) }
}