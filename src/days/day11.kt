package days
import util.Pos
import kotlin.math.max

fun day11(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val powerGrid = mutableMapOf<Pos, Int>()
    (1..300).forEach { x ->
        (1..300).forEach { y ->
            powerGrid.put(Pos(x, y), calcPowerLevel(x, y))
        }
    }
    (1..(300-2)).forEach { x ->
        (1..(300-2)).forEach { y ->
            val curr = Pos(x, y)
            powerGrid.put(curr, sumSquare(curr, powerGrid))
        }
    }
    val bestSquarePos = powerGrid.toList().maxBy { it.second }?.first?:Pos(-1, -1)
    return "Pt1: " + bestSquarePos.toString()
}

private fun pt2() : String {
    val powerGrid = mutableMapOf<Pos, IntArray>()
    var max = 0
    var posIndex = Pair(Pos(0,0), 0)
    for (x in (299 downTo 0)) {
        for (y in (299 downTo 0)) {
            val tmp = calcPowerLevelPt2(x, y, powerGrid)
            val tmpScore = powerGrid[tmp.first]?.get(tmp.second)?: Int.MIN_VALUE
            if ( tmpScore > max ) {
                max = tmpScore
                posIndex = tmp
            }
        }
    }
    println(powerGrid[posIndex.first]?.get(posIndex.second))
    return "Pt2: Pos: " + posIndex.first.move(1,1).toString() + " Index:" + posIndex.second.inc().toString()
}

private fun calcPowerLevel(x : Int, y : Int): Int {
    val serial = 3031
    val rackID = x + 10
    var powerLevel = rackID * y
    powerLevel += serial
    powerLevel *= rackID
    powerLevel = (powerLevel/100) % 10
    powerLevel -= 5
    return powerLevel
}

private fun sumSquare(topLeft : Pos, powerGrid : Map<Pos, Int>) : Int {
    var sum = 0
    (0..2).forEach { x ->
        (0..2).forEach { y ->
            sum += powerGrid.getOrDefault(topLeft.move(x, y), 0)
        }
    }
    return sum
}

private fun calcPowerLevelPt2(x : Int, y : Int, powerGrid: MutableMap<Pos, IntArray>) : Pair<Pos, Int> {
    val powerLevels = IntArray(300-max(x,y))
    powerLevels[0] = calcPowerLevel(x+1, y+1)
    val pos = Pos(x, y)
    var maxIndex = 0
    if (powerLevels.size > 1) {
        for (i in 1 .. (powerLevels.size - 1)) {
            val value = if (i == 1) {
                calcPowerLevel(pos.x+1, pos.y+1) + calcPowerLevel(pos.x+1, pos.y + 2) +
                        calcPowerLevel(pos.x + 2, pos.y + 1) + calcPowerLevel(pos.x + 2, pos.y + 2)
            } else {
                sumAnySquare(pos, i, powerGrid)
            }
            powerLevels[i] = value
            if (value > powerLevels[maxIndex]) {maxIndex = i}
        }
    }
    powerGrid.put(Pos(x, y), powerLevels)
    return Pair(pos, maxIndex)
}

private fun sumAnySquare(topLeft: Pos, offset : Int, powerGrid: Map<Pos, IntArray>) : Int {
    var sum = calcPowerLevel(topLeft.x+1, topLeft.y+1)
    sum += powerGrid[topLeft.move(1, 0)]?.get(offset - 1)?:0
    sum += powerGrid[topLeft.move(0, 1)]?.get(offset - 1)?:0
    sum -= powerGrid[topLeft.move(1, 1)]?.get(offset - 2)?:0
    sum += powerGrid[topLeft.move(offset, offset)]?.get(0)?:0
    return sum
}