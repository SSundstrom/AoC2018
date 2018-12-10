package days
import input.*
import java.util.SortedSet

fun day7(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val completed = mutableListOf<Char>()
    val workMap = getWorkMap()
    val available = getAvailable(workMap)
    while (available.isNotEmpty()) {
        val work = available.first()
        completed.add(work.id)
        available.remove(work)
        workMap.forEach { t, u ->
            val char = u.reqCompleted(work.id)
            if ( char != null) {
                available.add(workMap[char])
            }
        }
    }
    return "Pt1: " + completed.joinToString("")
}

private fun pt2() : String {

    val workMap = getWorkMap()
    val available = getAvailable(workMap)
    val completed = mutableListOf<Char>()
    val workers = mutableListOf<Task>()
    var timeSpent = 0

    while (available.isNotEmpty() || workers.isNotEmpty()) {
        timeSpent++
        // Assign new jobs
        while (available.isNotEmpty() && workers.size < 5) {
            val newJob = available.first()
            workers.add(newJob)
            available.remove(newJob)
        }
        // Work & checkDone
        val workIterator = workers.iterator()
        while (workIterator.hasNext()) {
            val task = workIterator.next()
            if (task.work()) {
                completed.add(task.id)
                workIterator.remove()
                workMap.forEach { _, u ->
                    val newAvailable = u.reqCompleted(task.id)
                    if (newAvailable != null) {
                        available.add(workMap[newAvailable])
                    }
                }
            }
        }
    }

    return "Pt2: " + timeSpent.toString()
}

fun getAvailable(workMap: MutableMap<Char, Task>): SortedSet<Task> {
    return workMap.toList().filter { it.second.preReqs.isEmpty() }.map { it.second }.toSortedSet()

}

class Task constructor(val id : Char) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return this.id.compareTo(other.id)
    }

    val preReqs = mutableListOf<Char>()
    var time = id.toInt() - 4 // 60 + x - 64 (because of int)

    fun addPreReq(preReq : Char) {
        preReqs.add(preReq)
    }

    fun work(): Boolean {
        time--
        return time == 0
    }

    fun reqCompleted(req : Char) : Char? {
        if (preReqs.isNotEmpty()) {
            preReqs.remove(req)
            if (preReqs.isEmpty()) { return id }
        }
        return null
    }
}

fun getWorkMap(): MutableMap<Char, Task> {
    val workMap = mutableMapOf<Char, Task>()
    getInput(7)
            .lines()
            .forEach { line ->
                val words = line.split(" ")
                val preReq = words[1].toCharArray()[0]
                val thenDo = words[7].toCharArray()[0]
                workMap.getOrPut(thenDo, { Task(thenDo) } ).addPreReq(preReq)
                workMap.putIfAbsent(preReq, Task(preReq))
            }
    return workMap
}