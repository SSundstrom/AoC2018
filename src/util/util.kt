package util

enum class Direction {
    NORTH, WEST, EAST, SOUTH
}

fun Direction.left() : Direction {
    return when (this) {
        Direction.NORTH -> Direction.WEST
        Direction.WEST -> Direction.SOUTH
        Direction.SOUTH -> Direction.EAST
        Direction.EAST -> Direction.NORTH
    }
}

fun Direction.right() : Direction {
    return when (this) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.NORTH

    }
}

class Pos constructor(val x : Int, val y : Int) : Comparable<Pos> {

    override fun compareTo(other: Pos): Int {
        val yComp = y.compareTo(other.y)
        if (yComp != 0) return yComp
        return x.compareTo(other.x)
    }

    override fun toString(): String {
        return "(" + this.x.toString() + ", " + this.y.toString() + ")"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Pos) {
            this.x == other.x && this.y == other.y
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return x * 883 + y * 919
    }

    fun move(x: Int, y: Int): Pos {
        return Pos(this.x + x, this.y + y)
    }

    fun move(dir : Direction?) : Pos {
        return when (dir) {
            Direction.NORTH -> Pos(x, y - 1)
            Direction.SOUTH -> Pos(x, y + 1)
            Direction.WEST -> Pos(x - 1, y)
            Direction.EAST -> Pos(x + 1, y)
            else -> Pos(x, y)
        }
    }

}