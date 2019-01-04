package days
import input.getInput
import util.Array2D
import util.Pos

fun day18(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(40) + pt2()
}

private fun pt1() : String {
//    Parse
    val initList = mutableListOf<Pair<Pos, F>>()
    val input = getInput(18).lines()
    val ySize = input.size; val xSize = input.first().length
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            initList.add(Pair(Pos(x,y), when (c) {
                '#' -> F.LUMBER
                '.' -> F.OPEN
                '|' -> F.TREE
                else -> error("$c should not appear in input")
            }))
        }
    }
    var field = Array2D<F>(xSize, ySize)
    initList.forEach { field[it.first] = it.second }
    for (i in 1 .. 10) {
        val newField = Array2D<F>(xSize, ySize)
        field.forEachIndexed {
            x, y, _ ->
            val tile = newTile(Pos(x, y), field)
            newField[x,y] = tile
        }
        field = newField
    }

    var tree = 0
    var lumber = 0
    field.forEach { when (it) {
        F.TREE -> tree++
        F.LUMBER -> lumber++
    } }

    return "Pt1: ${tree * lumber}"
}

private fun pt2() : String {
    val timeReq = 1000000000
    val initList = mutableListOf<Pair<Pos, F>>()
    val input = getInput(18).lines()
    val ySize = input.size; val xSize = input.first().length
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            initList.add(Pair(Pos(x,y), when (c) {
                '#' -> F.LUMBER
                '.' -> F.OPEN
                '|' -> F.TREE
                else -> error("$c should not appear in input")
            }))
        }
    }
    var field = Array2D<F?>(xSize, ySize)
    initList.forEach { field[it.first] = it.second }

    val a = Array2D<F?>(xSize, ySize)
    initList.forEach { a[it.first] = it.second }

    val solutions = mutableMapOf<Array2D<F?>, Int>()
    var min = 1
    while (min <= timeReq) {
        val newField = Array2D<F>(xSize, ySize)
        field.forEachIndexed {
            x, y, _ ->
            val tile = newTile(Pos(x, y), field)
            newField[x,y] = tile
        }
        if (newField in solutions) {
            val firstOccurance = solutions[newField]?: error("field not found in solutions.")
            val loopSize = min - firstOccurance
            while (min + loopSize <= timeReq) min += loopSize
            solutions.clear()
        }
        solutions[newField] = min
        field = newField
        min++
    }

    var tree = 0
    var lumber = 0
    field.forEach { when (it) {
        F.TREE -> tree++
        F.LUMBER -> lumber++
    } }

    return "Pt2: ${tree * lumber}"
}

private enum class F {
    OPEN, TREE, LUMBER;

    override fun toString(): String {
        return when (this) {
            OPEN -> "."
            TREE -> "|"
            LUMBER -> "#"
        }
    }
}

private fun getSurroundings(center : Pos, area : Array2D<F?>) : Surrounding {
    val res = Surrounding()
    for (x in -1 .. 1) {
        for (y in -1 .. 1) {
            val pos = center.move(x, y)
            if (area.contains(pos) && (x != 0 || y != 0)) {
                res.inc(area[center.move(x, y)])
            }
        }
    }
    return res
}

private fun newTile(centerPos : Pos, area: Array2D<F?>) : F {
    val surroundings = getSurroundings(centerPos, area)
    val center = area[centerPos]

    return when (center) {
        F.OPEN -> if (surroundings.tree >= 3) F.TREE else F.OPEN
        F.TREE -> if (surroundings.lumber >= 3) F.LUMBER else F.TREE
        F.LUMBER -> if (surroundings.lumber >= 1 && surroundings.tree >= 1) F.LUMBER else F.OPEN
        null -> error("Found null in field.")
    }
}

private class Surrounding {
    var tree = 0
    var open = 0
    var lumber = 0

    fun get(tile : F) : Int {
        return when (tile) {
            F.TREE -> tree
            F.OPEN -> open
            F.LUMBER -> lumber
        }
    }

    fun inc(tile  : F?) {
        when (tile) {
            F.TREE -> tree++
            F.OPEN -> open++
            F.LUMBER -> lumber++
        }
    }
}