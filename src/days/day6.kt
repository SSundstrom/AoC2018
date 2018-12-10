package days
import input.*
import kotlin.math.abs
import Aux.*

fun day6(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\n" + pt2()
}

private fun getCoordsmap() : Map<Int, Pos> {
    val indata = getInput(6).lines()
    val coordsMap : Map<Int, Pos> = indata.zip((0..indata.size)).map { tup ->
        val ints = tup.first.split(", ").map { it.toInt() }
        val y = ints[0]
        val x = ints[1]
        return@map Pair(tup.second, Pos(x, y))
    }.toMap()
    return coordsMap
}

private fun getMaxCoords(coordsMap: Map<Int, Pos>) : Pair<Int, Int> {
    val xMax = coordsMap.values.maxBy { it.x }?.x?:-1
    val yMax = coordsMap.values.maxBy { it.y }?.y?:-1
    return Pair(xMax, yMax)
}

private fun pt1() : String {

    val coordsMap = getCoordsmap()
    val max = getMaxCoords(coordsMap)

    val map = makeMap(max.first, max.second, coordsMap, Map<Int, Pos>::getOwner)

    val infinites : Set<Int> = getInf(coordsMap, max.first, max.second)

    val res = coordsMap.keys.filter { !infinites.contains(it) }.map { id ->
        Pair(id, map.values.count({it == id}))
    }.toMutableSet()

    return "Pt1: " + res.maxBy { it.second }.toString()
}

private fun pt2() : String {

    val coordsMap = getCoordsmap()
    val max = getMaxCoords(coordsMap)

    val middleMap = makeMap(max.first, max.second, coordsMap, Map<Int, Pos>::getCentral)
    val pt2res = middleMap.values.filter { it == 1 }.count()

    return "Pt2: " + pt2res.toString()
}

fun Map<Int, Pos>.getOwner(other: Pos) : Int {
    var min = Int.MAX_VALUE
    var retVal = -1
    val firstPass = this.iterator()
    while (firstPass.hasNext()) {
        val current = firstPass.next()
        val distance = current.value.distanceTo(other)
        if (distance < min) {min = distance; retVal = current.key}
    }
    val secondPass = this.iterator()
    while (secondPass.hasNext()) {
        val current = secondPass.next()
        if (current.key != retVal) {
            val distance = current.value.distanceTo(other)
            if (distance == min) {return -1}
        }
    }
    return retVal
}

fun Map<Int, Pos>.getCentral(other: Pos) : Int {
    val MAX_DIST = 10000
    var totDist = 0
    this.values.forEach {
        totDist += it.distanceTo(other)
        if ( totDist >= MAX_DIST ) {
            return 0
        }
    }
    return 1
}

fun Pos.distanceTo(other: Pos): Int {
    return abs(other.x - this.x) + abs(other.y - this.y)
}

fun makeMap(xSize: Int, ySize: Int, coordinates : Map<Int, Pos>, op : (Map<Int, Pos>.(Pos) -> Int)) : Map<Pos, Int?> {
    val tmp = mutableMapOf<Pos, Int?>()
    (0..xSize).map { x ->
        (0..ySize).forEach { y ->
            val pos = Pos(x, y)
            val owner = coordinates.op(pos)
            tmp.put(pos, owner)
        }
        // println(tmp.filter { it.key.x == x }.values)
    }
    return tmp.toMap()
}

fun getInf(map: Map<Int, Pos>, xSize : Int, ySize : Int): Set<Int> {
    val infSet = mutableSetOf<Int>()
    (0..xSize).forEach { x ->
        infSet.add(map.minBy { it.value.distanceTo(Pos(x, 0)) }?.key?:-1)
        infSet.add(map.minBy { it.value.distanceTo(Pos(x, ySize)) }?.key?:-1)
    }
    (0..ySize).forEach { y ->
        infSet.add(map.minBy { it.value.distanceTo(Pos(1, y)) }?.key?:-1)
        infSet.add(map.minBy { it.value.distanceTo(Pos(xSize, y)) }?.key?:-1)
    }
    return infSet
}