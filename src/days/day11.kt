package days
import util.Pos
import kotlin.math.max

fun day11(pt: Int): String {
    val powerGrid = createPowerGrid()
    when (pt) {
        1 -> return pt1(powerGrid)
        2 -> return pt2(powerGrid)
    }
    return pt1(powerGrid) + "\t" + pt2(powerGrid)
}

private fun pt1(powerGrid: Map<Pos, IntArray>) : String {
    val pair = powerGrid.maxBy { it.value[0] }
    val pos = pair?.key?.move(1,1)
    return "Pt1: " + pos.toString()
}

private fun pt2(powerGrid: Map<Pos, IntArray>) : String {
    val a = powerGrid.maxBy { it.value.max()?:Int.MIN_VALUE }
    val array = a?.value?: intArrayOf()
    var maxIndex = -1
    var max = 0
    array.forEachIndexed { index, value ->
        if (value > max) {maxIndex = index; max = value}
    }
    return "Pt2: Pos: " + a?.key.toString() + " Index:" + maxIndex.toString()
}

private fun createPowerGrid() : Map<Pos, IntArray> {
    val powerGrid = mutableMapOf<Pos, IntArray>()
    for (x in (299 downTo 0)) {
        for (y in (299 downTo 0)) {
            fillPowerGrid(x, y, powerGrid)
        }
    }
    return powerGrid.toMap()
}

private fun calcPowerLevel(x : Int, y : Int): Int {
    val serial = 3031
    val rackID = x.inc() + 10
    var powerLevel = rackID * y.inc()
    powerLevel += serial
    powerLevel *= rackID
    powerLevel = (powerLevel/100) % 10
    powerLevel -= 5
    return powerLevel
}

private fun fillPowerGrid(x : Int, y : Int, powerGrid: MutableMap<Pos, IntArray>) {
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
}

private fun sumAnySquare(topLeft: Pos, offset : Int, powerGrid: Map<Pos, IntArray>) : Int {
    var sum = calcPowerLevel(topLeft.x+1, topLeft.y+1)
    sum += powerGrid[topLeft.move(1, 0)]?.get(offset - 1)?:0
    sum += powerGrid[topLeft.move(0, 1)]?.get(offset - 1)?:0
    sum -= powerGrid[topLeft.move(1, 1)]?.get(offset - 2)?:0
    sum += powerGrid[topLeft.move(offset, offset)]?.get(0)?:0
    return sum
}