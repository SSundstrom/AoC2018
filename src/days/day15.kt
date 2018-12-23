package days
import input.*
import util.Pos
import java.util.*
import kotlin.Comparator
import util.Direction as D

fun day15(pt: Int): String {
    when (1) {
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
            field.populate(c, Pos(x, y))
        }
    }
    println(field)
    // Round
    var round = 0
    while (field.bothTeamsAlive()) {
        // Setup?

        val sortedSoldiers = field.getTurnOrder()
        var completedRound = true
        // Step
        for (i in 0 until sortedSoldiers.size) {
            // Actions for each soldier
            val soldier = sortedSoldiers[i]
            if (soldier.isDead()) continue
            val opponents = field.getOpponents(soldier.team)
            if (opponents.isEmpty()) { completedRound = false; break }
            soldier.move(field)
            val target = soldier.findTarget(opponents)
            target?.takeDamage(soldier.attack)
            if (target?.isDead() == true) field.remove(target)
        }
        if (completedRound) round++
//        println("$round : ")
//        println(field)
//        sortedSoldiers.forEach { print("${it.team.toString().first()}${it.health}, ") }
//        println()
    }

    println("".padEnd(20, '-'))
    println(field)
    var hpLeft = 0
    field.elfs.forEach { hpLeft += it.health; print(" E${it.health}") }
    field.goblins.forEach { hpLeft += it.health; print(" G${it.health}") }
    println(" = $hpLeft with round $round completed")

    return (hpLeft * round).toString()
}

private fun pt2() : String {
    return "unimplemented"
}

private class Soldier constructor(var pos : Pos, val team : T) : Comparable<Soldier> {
    override fun compareTo(other: Soldier): Int {
        return pos.compareTo(other.pos) //To change body of created functions use File | Settings | File Templates.
    }

    var health = 200
    val attack = 3

    fun takeDamage(damage : Int) {
        this.health -= damage
    }

    fun move(field : Board ) {
//        if (pos.y == 8) {
//            print("-")
//        }
        val opponents = field.getOpponents(team)
        val obstacles = field.getObstacles()
        if (inRange(opponents).isNotEmpty()) return
        val nextToOpponent = mutableListOf<Pos>()
        opponents.forEach { op ->
            op.getSurrounding().forEach { pos ->
                nextToOpponent.add(pos) } }
        val inRange = nextToOpponent.filter { it !in obstacles }

        val reachable = inRange.mapNotNull { pathTo(it, obstacles) }

        val shortestPathLength = reachable.map { it.second.size }.min()
        val nearest = reachable.filter { it.second.size == shortestPathLength }.minBy { it.first }

        pos = pos.move(nearest?.first)
    }

    fun pathTo(target : Pos, obstacles : List<Pos>) : Pair<D, List<D>>? {
        val directions = listOf(D.NORTH, D.WEST, D.EAST, D.SOUTH) // This might be very important
        val tup : List<Pair<D, List<D>>> = directions.mapNotNull { dir ->
            val availablePos = PriorityQueue<Pos>(DistanceTo(target))
            val solutions = mutableMapOf<Pos, List<D>>()
            val startPos = pos.move(dir)
            if (startPos in obstacles) { return@mapNotNull null }
            availablePos.add(startPos)
//            for (i in 0 until directions.size) {
//                val step = startPos.move(directions[i])
//                if (step !in obstacles) {
//                    solutions[step] = listOf(directions[i])
//                    availablePos.add(step)
//                }
//            }
            while (availablePos.isNotEmpty()) {
                val currPos = availablePos.poll()
                val path = solutions[currPos] ?: emptyList()
                if (target == currPos) return@mapNotNull Pair(dir, path)
                directions.forEach { direction ->
                    val newPos = currPos.move(direction)
                    if (newPos !in obstacles) {
                        val newPath = path.plus(direction)
                        val oldPath = solutions[newPos]
                        if (oldPath.isNullOrEmpty()) {
                            // Pos has not been reachable before
                            solutions[newPos] = newPath
                            availablePos.add(newPos)
                        } else {
                            if (oldPath.size > newPath.size) {
                                // The new path is shorter
                                solutions[newPos] = newPath
                            }
                        }
                    }
                }
            }
            return@mapNotNull null
        }
        val shortest = tup.minBy { it.second.size }
        return tup.filter { it.second.size == shortest?.second?.size }.minBy { it.first }
    }

    fun isDead() : Boolean {
        return health <= 0
    }

    fun inRange(opponents: List<Soldier>) : List<Soldier> {
        return opponents.filter { it.pos.distanceTo(this.pos) == 1 }
    }

    fun findTarget(opponents : List<Soldier>) : Soldier? {
        return inRange(opponents).sortedBy { it.health }.firstOrNull()
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

    fun populate (char : Char, pos : Pos) {
        when (char) {
            '#' -> walls.add(pos)
            'E' -> elfs.add(Soldier(pos, T.Elf))
            'G' -> goblins.add(Soldier(pos, T.Goblin))
        }
    }

}

private class DistanceTo constructor(val target : Pos) : Comparator<Pos> {

    override fun compare(o1: Pos?, o2: Pos?) : Int {
        var distanceDiff = 0
        var naturalOrdering = 0
        if (o1 != null) {
            distanceDiff -= o1.distanceTo(target)
            naturalOrdering = -1
        }
        if (o2 != null) {
            distanceDiff += o2.distanceTo(target)
            naturalOrdering = o1?.compareTo(o2)?:1
        }
        return if (distanceDiff == 0) {
            naturalOrdering
        } else {
            distanceDiff
        }

    }

}

private enum class T {
    Elf, Goblin;
}