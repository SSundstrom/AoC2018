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

fun <E> MutableList<E>.pop() : E {
    val a = this.first()
    this.removeAt(0)
    return a
}

class Array2D<T> (val xSize: Int, val ySize: Int, val array: Array<Array<T>>) {

    companion object {

        inline operator fun <reified T> invoke() = Array2D(0, 0, Array(0) { emptyArray<T>() })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int) =
                Array2D(xWidth, yWidth, Array(xWidth) { arrayOfNulls<T>(yWidth) })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, default : T) =
                Array2D(xWidth, yWidth, Array(xWidth) { Array(yWidth) {default} })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, operator: (Int, Int) -> (T)): Array2D<T> {
            val array = Array(xWidth) { x ->
                Array(yWidth) {y -> operator(x, y)}
            }
            return Array2D(xWidth, yWidth, array)
        }
    }

    operator fun get(pos: Pos): T {
        return get(pos.x, pos.y)
    }

    operator fun get(x: Int, y: Int): T {
        return array[x][y]
    }

    operator fun set(x: Int, y: Int, t: T) {
        array[x][y] = t
    }

    operator fun set(pos: Pos, t: T) {
        set(pos.x, pos.y, t)
    }


    inline fun forEach(operation: (T) -> Unit) {
        array.forEach { it.forEach { operation.invoke(it) } }
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { x, p -> p.forEachIndexed { y, t -> operation.invoke(x, y, t) } }
    }

    fun contains(x: Int, y: Int) : Boolean {
        return (x >= 0 && y >= 0 && x < xSize && y < ySize)
    }

    fun contains(pos : Pos) : Boolean {
        return this.contains(pos.x, pos.y)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until ySize) {
            sb.append(y); sb.append(": ")
            for (x in 0 until xSize) {
                sb.append(array[x][y])
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    override fun hashCode(): Int {
        var hash = 0
        forEachIndexed { x, y, t -> hash += x * 119 + y * 123 + t.hashCode() }
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other is Array2D<*>) {
            if (other.xSize == this.xSize && other.ySize == this.ySize) {
                other.forEachIndexed { x, y, any ->
                    if (this[x, y] != any) return false
                }
                return true
            }
        }
        return false
    }

}