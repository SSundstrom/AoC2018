package days
import input.*
import util.Pos
import java.util.*
import kotlin.Comparator
import kotlin.math.abs
import util.Direction as D

fun day15(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(50) + pt2()
}

private fun pt1() : String {
    // Init
    val input = getInput(15).lines()
    val field = Board(input.first().length, input.size)
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            field.populate(c, Pos(x, y), 3)
        }
    }
    // Round
    var round = 0
    while (field.bothTeamsAlive()) {
        // Setup?
        val sortedSoldiers = field.getTurnOrder()
        var completedRound = true
        // Step
        for (i in 0 until sortedSoldiers.size) {
            // Actions for each soldier
            completedRound = takeTurn(sortedSoldiers[i], field)
        }
        if (completedRound) round++
    }
    var hpLeft = 0
    field.goblins.union(field.elfs).forEach { hpLeft += it.health }
//    println("$hpLeft with round $round completed")
    return "Pt1: ${hpLeft * round}"
}

private fun pt2() : String {
    // Init
    val input = getInput(15).lines()
    var power = 100
    var offset = 0
    var res : Pair<Int, Int>
    while (power > 1) {
        res = runWith(power, offset, input)
        offset += (res.first-1)*power
        power /= 2
    }
    res = runWith(1, offset, input)
    return "Pt2: ${res.second}"
}

private fun runWith(power : Int, offset : Int, input : List<String>) : Pair<Int, Int> {
    var hpLeft = 0
    var round = 0
    var anElfDied = true
    var str = 0
    while (anElfDied) {
        str++
        val field = Board(input.first().length, input.size)
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                field.populate(c, Pos(x, y), str*power + offset)
            }
        }
        // Round
        round = 0
        val elfCount = field.elfs.size
        while (field.bothTeamsAlive() && elfCount == field.elfs.size) {
            // Setup?
            val sortedSoldiers = field.getTurnOrder()
            var completedRound = true
            // Step
            for (i in 0 until sortedSoldiers.size) {
                // Actions for each soldier
                completedRound = takeTurn(sortedSoldiers[i], field)
            }
            if (completedRound) round++
        }
        anElfDied = elfCount != field.elfs.size
        hpLeft = 0
        field.goblins.union(field.elfs).forEach { hpLeft += it.health }
    }
    return Pair(str, hpLeft * round)
}

private fun takeTurn( soldier : Soldier,  field: Board) : Boolean {
    if (soldier.isDead()) return true
    val opponents = field.getOpponents(soldier.team)
    if (opponents.isEmpty()) { return false }
    soldier.move(field)
    val target = soldier.findTarget(opponents)
    target?.takeDamage(soldier.attack)
    if (target?.isDead() == true) field.remove(target)
    return true
}

private class Soldier constructor(var pos : Pos, val team : T, val attack : Int) : Comparable<Soldier> {
    constructor(pos : Pos, team : T) : this(pos, team, 3)

    override fun compareTo(other: Soldier): Int {
        return pos.compareTo(other.pos) //To change body of created functions use File | Settings | File Templates.
    }

    var health = 200

    fun takeDamage(damage : Int) {
        this.health -= damage
    }

    fun move(field : Board ) {
        val opponents = field.getOpponents(team)
        val obstacles = field.getObstacles()
        if (inRange(opponents).isNotEmpty()) return
        val inRange = mutableListOf<Pos>()
        opponents.forEach { op ->
            op.getSurrounding().forEach { pos ->
                if (pos !in obstacles) inRange.add(pos) } }
//        println("start path")
        val reachable = inRange.mapNotNull { pathTo(it, obstacles) }
//        println("end path")
        val shortestPathLength = reachable.map { it.size }.min()
        val nearest = reachable
                .filter { it.size == shortestPathLength }
                .minBy { it.fold(pos) { curr, dir -> curr.move(dir)} }

        pos = pos.move(nearest?.first())
    }

    fun pathTo(target : Pos, obstacles : List<Pos>) : List<D>? {
        val directions = listOf(D.NORTH, D.WEST, D.EAST, D.SOUTH)
        val possibleMoves = PriorityQueue<Pos>(DistanceTo(target))
        val paths = mutableMapOf<Pos, List<D>>()
        var minPathDist = Int.MAX_VALUE
        directions.forEach {
            if (pos.move(it) !in obstacles) {
                paths[pos.move(it)] = listOf(it)
                possibleMoves.add(pos.move(it))
            }
        }
//        var counter = 0
        while (possibleMoves.isNotEmpty()) {
//            counter++
//            print("$counter-")
            val curr = possibleMoves.poll()
            val pathToCurr = paths[curr]?: error("There is no path to $curr")
            directions.forEach {
                val newPos = curr.move(it)
                if (newPos !in obstacles
                        && pathToCurr.size + 1 <= minPathDist ) {
                    val newPath = pathToCurr.plus(it)
                    val oldPath = paths[newPos]
                    if (oldPath == null || oldPath.size >= newPath.size) {
                        when {
                            oldPath == null -> {
                                paths[newPos] = newPath
                                possibleMoves.add(newPos) }
                            oldPath.size > newPath.size -> {
                                paths[newPos] = newPath
                                possibleMoves.add(newPos) }
                            oldPath.first() > newPath.first() -> {
                                paths[newPos] = newPath
                                possibleMoves.add(newPos) }
                        }
                    }
                    if (newPos == target) {
                        minPathDist = newPath.size
                    }
                }
            }
        }
//        println()
        return paths[target]
    }

    fun isDead() : Boolean {
        return health <= 0
    }

    fun inRange(opponents: List<Soldier>) : List<Soldier> {
        return opponents.filter { it.pos in getSurrounding() }
    }

    fun findTarget(opponents : List<Soldier>) : Soldier? {
        val inRange = inRange(opponents)
        val minHealth = inRange.map { it.health }.min()
        return inRange.filter { it.health == minHealth }.minBy { it.pos }
    }

    fun getSurrounding() : List<Pos> {
        return listOf(
                pos.move(D.NORTH),
                pos.move(D.EAST),
                pos.move(D.SOUTH),
                pos.move(D.WEST)
        )
    }
}

private class Board constructor(val width : Int, val height : Int){
    val elfs = mutableListOf<Soldier>()
    val goblins = mutableListOf<Soldier>()
    val walls = mutableListOf<Pos>()

    fun getTurnOrder() : List<Soldier> {
        return goblins.union(elfs).toSortedSet().toList()
    }

    fun getOpponents(myTeam : T) : List<Soldier> {
        return when (myTeam) {
            T.Elf -> goblins
            T.Goblin -> elfs
        }
    }

    fun bothTeamsAlive() : Boolean {
        return  !(elfs.isEmpty() || goblins.isEmpty())
    }

    fun getObstacles() : List<Pos> {
        return elfs.union(goblins).map { it.pos }.union(walls).toList()
    }

    fun remove( item : Soldier? ) : Boolean {
        if (item != null) {
            when (item.team) {
                T.Elf -> { elfs.remove(item); return elfs.isEmpty() }
                T.Goblin -> { goblins.remove(item); return goblins.isEmpty() }
            }
        }
        return false
    }

    override fun toString(): String {
        val row = MutableList(width) {'.'}
        val matrix = MutableList(height) {row.toMutableList()}
        elfs.forEach { matrix[it.pos.y][it.pos.x] = 'E' }
        goblins.forEach { matrix[it.pos.y][it.pos.x] = 'G' }
        walls.forEach { matrix[it.y][it.x] = '#' }
        val lines = matrix.map { it.joinToString("") }
        return lines.joinToString("\n")
    }

    fun populate (char : Char, pos : Pos, str : Int) {
        when (char) {
            '#' -> walls.add(pos)
            'E' -> elfs.add(Soldier(pos, T.Elf, str))
            'G' -> goblins.add(Soldier(pos, T.Goblin))
        }
    }

}

private class DistanceTo constructor(val target : Pos) : Comparator<Pos> {

    override fun compare(o1: Pos?, o2: Pos?) : Int {
        var distanceDiff = 0
        var naturalOrdering = 0
        if (o1 != null) {
            distanceDiff += o1.distanceTo(target)
            naturalOrdering = -1
        }
        if (o2 != null) {
            distanceDiff -= o2.distanceTo(target)
            naturalOrdering = o1?.compareTo(o2)?:1
        }
        return if (distanceDiff == 0) {
            naturalOrdering
        } else {
            distanceDiff / abs(distanceDiff)
        }

    }

}

private enum class T {
    Elf, Goblin;
}