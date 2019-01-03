package days
import input.*
import util.*
import util.Direction as D

fun day17(pt: Int): String {

    val ground = getGround()

    when (pt) {
        1 -> return pt1(ground)
        2 -> return pt2(ground)
    }
    return pt1(ground).padEnd(40) + pt2(ground)
}

private fun getGround() : Array2D<G> {
    val clay = mutableListOf<Pos>()
    var center = 500
    var xmin = center
    var xmax = center
    var ymax = 0
    getInput(17).lines().map { it.split("=", ", ", "..") }.forEach {
        //        [var, coord, var, coord, coord]
//        ["x", "456", "y", "40", "60"]
        val min = it[3].toInt(); val max = it[4].toInt()
        if (it[0] == "x") {
            val x = it[1].toInt()
            for (y in min..max) {
                clay.add(Pos(x,y))
            }
            if (x < xmin) xmin = x
            if (x > xmax) xmax = x
            if (max > ymax) ymax = max
        } else {
            val y = it[1].toInt()
            for (x in min..max) {
                clay.add(Pos(x,y))
            }
            if (y > ymax) ymax = y
            if (min < xmin) xmin = min
            if (max > xmax) xmax = max
        }
    }
    xmax++;xmax++
    xmin--;xmin--

    val ground = Array2D(xmax - xmin + 2, ymax + 2, G.SAND)
    val source = Pos(center-xmin, 0)
    ground[source] = G.SOURCE
    clay.forEach { ground[it.x - xmin, it.y] = G.CLAY }
    flow(source.move(D.SOUTH), ground, ymax)
    return ground
}
private fun pt1(ground : Array2D<G>) : String {
    var topMostY = 0
    while (G.CLAY !in (0 until ground.xSize).map { ground[it, topMostY] }) { topMostY++ }
    var counter = 1-topMostY // We discard all tiles above the topmost clay-tile
    ground.forEach { if (it == G.WATER || it == G.FALLING) counter++}

    return "Pt1: $counter"
}

private fun pt2(ground : Array2D<G>) : String {
    var counter = 0
    ground.forEach { if (it == G.WATER) counter++ }
    return "Pt2: $counter"
}

private fun flow(curr : Pos, ground : Array2D<G>, ymax : Int) {
    val layer = WaterLayer(curr)
    val res = layer.fill(ground, ymax)
    layer.positions.forEach { ground[it] = res?:G.FALLING }
    when (res) {
        G.FALLING -> layer.sinks.forEach {
            if (ground[it] != G.WATER) flow(it.move(D.SOUTH), ground, ymax)
        }
        G.WATER -> flow(layer.source.move(D.NORTH), ground, ymax)
        null -> return
        else -> error("res: $res\tcurr: $curr\nground: $ground")
    }
}

private class WaterLayer constructor(val source : Pos) {
    val positions = mutableSetOf(source)
    var east = source
    var west = source
    val sinks = mutableSetOf<Pos>()

    fun fill(ground : Array2D<G>, ymax : Int) : G? {
        if (source.y >= ymax) {
            return null
        }

        fun cont(pos: Pos) : Boolean {
            val south = pos.move(D.SOUTH)
            return (ground[pos] == G.SAND || ground[pos] == G.FALLING) &&
                    (ground[south] != G.SAND && ground[south] != G.FALLING)
        }

        while (cont(west)) {
            positions.add(west)
            west = west.move(D.WEST)
        }

        while (cont(east)) {
            positions.add(east)
            east = east.move(D.EAST)
        }
        if (ground[west.move(D.SOUTH)] == G.FALLING) return null
        if (ground[east.move(D.SOUTH)] == G.FALLING) return null
        if (ground[west.move(D.SOUTH)] == G.SAND) sinks.add(west)
        if (ground[east.move(D.SOUTH)] == G.SAND) sinks.add(east)
        if (sinks.isNotEmpty()) {
            positions.addAll(sinks)
            return G.FALLING
        }
        if (ground[west] == G.CLAY && ground[east] == G.CLAY) {
            return G.WATER
        }
        val range = 25
        for (y in -range .. range) {
            for (x in -range .. range) {
                if (x == 0 && y == 0) {
                    print("O")
                } else {
                    print(ground[source.move(x, y)])
                }
            }
            println()
        }
        error("The positions is not shaped correctly from $source\n$this")
    }

    override fun toString(): String {
        return "Source: $source\tEast: $east\tWest: $west\nLayer: ${positions.joinToString(", ") }"
    }
}

private enum class G {
    WATER, FALLING, SAND, CLAY, SOURCE;

    override fun toString(): String {
        return when (this) {
            CLAY -> "#"
            FALLING -> "|"
            SAND -> "."
            SOURCE -> "+"
            WATER -> "~"
        }
    }

}
