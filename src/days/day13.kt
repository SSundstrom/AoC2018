package days
import input.*
import util.Direction
import util.Pos
import util.left
import util.right

fun day13(pt: Int): String {
    val carts = init()
    when (pt) {
        1 -> return pt1(carts.toMutableList())
        2 -> return pt2(carts.toMutableList())
    }
    return pt1(carts).padEnd(40) + pt2(carts).toString()
}

private fun pt1(carts : MutableList<Cart>) : String {
    val size = carts.size
    while (true) {
        for (i in 0 until carts.size) {
            carts[i].move()
            if (carts.map { it.pos }.toSet().size < size) {
                return "Pt1: ${carts[i].pos}"
            }
        }
    }
}

private fun pt2(carts: MutableList<Cart>) : String {
    while (carts.size > 1) {
        val removed = mutableListOf<Cart>()
        for (i in 0 until carts.size) {
            val cart = carts[i]
            if (cart !in removed) {
                val newPos = cart.move()
                val samePos = carts.filter { it.pos == newPos && it !in removed}
                if (samePos.size > 1) {samePos.forEach { removed.add(it)} } //; carts.forEach { println(it)};println()}
            }
        }
        removed.forEach { carts.remove(it) }
        carts.sort()
    }
    return "Pt2: ${carts.first().pos}"
}

val inter = mutableSetOf<Pos>()
val NE = mutableSetOf<Pos>()
val NW = mutableSetOf<Pos>()
val SE = mutableSetOf<Pos>()
val SW = mutableSetOf<Pos>()

private fun init() : MutableList<Cart> {
    val carts = mutableListOf<Cart>()
    getInput(13).lines().forEachIndexed {
        y, line ->  line
            .forEachIndexed {
                x, char ->
                when (char) {
                    '+' -> inter.add(Pos(x, y))
                    '/' -> {SE.add(Pos(x,y)); NW.add(Pos(x,y))}
                    '\\' -> { NE.add(Pos(x,y)); SW.add(Pos(x,y))}
                    '>' -> carts.add(Cart(Pos(x,y), Direction.EAST))
                    '<' -> carts.add(Cart(Pos(x,y), Direction.WEST))
                    '^' -> carts.add(Cart(Pos(x,y), Direction.NORTH))
                    'v' -> carts.add(Cart(Pos(x,y), Direction.SOUTH))
                }
            }
    }
    return carts
}

private class Cart constructor(var pos : Pos, var direction : Direction) : Comparable<Cart> {
    private var turnOrder = 0

    override fun compareTo(other: Cart): Int {
        val yDiff = pos.y.compareTo(other.pos.y)
        if (yDiff == 0) {
            return pos.x.compareTo(other.pos.x)
        }
        return yDiff
    }

    fun move() : Pos {
        pos = pos.move(direction)
        if (pos in inter) {
            direction = when (turnOrder) {
                0 -> direction.left()
                1 -> direction
                else -> direction.right()
            }
            turnOrder = (turnOrder+1)%3
        } else {
            when (direction) {
                Direction.NORTH -> {
                    when (pos) {
                        in SW -> direction = Direction.WEST
                        in SE -> direction = Direction.EAST
                    }
                }
                Direction.SOUTH -> {
                    when (pos) {
                        in NW -> direction = Direction.WEST
                        in NE -> direction = Direction.EAST
                    }
                }
                Direction.WEST -> {
                    when (pos) {
                        in NE -> direction = Direction.NORTH
                        in SE -> direction = Direction.SOUTH
                    }
                }
                Direction.EAST -> {
                    when (pos) {
                        in NW -> direction = Direction.NORTH
                        in SW -> direction = Direction.SOUTH
                    }
                }
            }
        }
        return pos
    }

    override fun toString(): String {
        val sign = when (direction) {
            Direction.NORTH -> "^"
            Direction.WEST -> "<"
            Direction.SOUTH -> "v"
            Direction.EAST -> ">"
        }
        return "$sign at $pos"
    }
}