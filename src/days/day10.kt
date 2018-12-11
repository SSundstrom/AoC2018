package days
import input.*
import util.*

fun day10(pt: Int): String {
    val res = solveProblem()
    when (pt) {
        1 -> return pt1(res)
        2 -> return pt2(res)
    }
    return pt1(res) + "\t" + pt2(res)
}

private fun pt1(res : Pair<Int, String>) : String {
    return res.second
}

private fun pt2(res : Pair<Int, String>) : String {
    return "Seconds: " + res.first.toString()
}

fun solveProblem(): Pair<Int, String> {
    val points = getInput(10).lines().map {
        val data = Regex("-*[0-9]+").findAll(it).toList().map { it.value.toInt() }
        return@map Point(data[0], data[1], data[2], data[3])
    }

    var iteration = 0
    var corners = findCorners(points)
    var newDiameter = corners.first.distanceTo(corners.second)
    var diameter = Int.MAX_VALUE

    while (newDiameter < diameter) {
        diameter = newDiameter
        iteration++
        points.forEach {
            it.move()
        }
        corners = findCorners(points)
        newDiameter = corners.first.distanceTo(corners.second)
    }

    return Pair(iteration, printPoints(points, corners.second, corners.first))
}

private class Point constructor(var xPos : Int, var yPos : Int, val xVelocity : Int, val yVelocity : Int) : Comparable<Point> {
    override fun toString(): String {
        return "Pos: (" + xPos.toString() + ", " + yPos.toString()+
                ") Vel: (" + xVelocity.toString() + ", " + yVelocity.toString() + ")"
    }

    fun move(): Pos {
        xPos += xVelocity
        yPos += yVelocity
        return Pos(xPos, yPos)
    }

    override fun compareTo(other: Point): Int {
        val xDiff = other.xPos - this.xPos
        val yDiff = other.yPos - this.yPos
        if (yDiff == 0) {
            return xDiff
        }
        return yDiff
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Point) {
            other.yPos == this.yPos && other.xPos == this.xPos
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return xPos*11 + yPos*31
    }

    fun toPos(): Pos {
        return Pos(xPos, yPos)
    }

}

private fun printPoints(points : List<Point>, topLeft: Pos, bottomRight : Pos): String {
    val posSet = mutableSetOf<Pos>()
    val offset = 4
    val retString = mutableListOf<String>()
    println(topLeft.toString() + " - " + bottomRight.toString())
    points.forEach { posSet.add(Pos(it.xPos, it.yPos)) }
    ((topLeft.y) ..  (bottomRight.y)).forEach { y ->
        val line = mutableListOf<Char>()
        ((topLeft.x-offset) .. (bottomRight.x + offset)).forEach { x ->
            if (posSet.contains(Pos(x, y))) {
                line.add('#')
            } else {
                line.add('.')
            }
        }
        retString.add(line.joinToString(""))
    }
    return retString.joinToString("\n")
}

private fun findCorners(points: List<Point>): Pair<Pos, Pos> {
    val sorted = points.sorted()
    return Pair(sorted.first().toPos(), sorted.last().toPos())
}