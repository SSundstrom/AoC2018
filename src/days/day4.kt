package days
import input.*

fun day4(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    return "unimplemented"
}

private fun pt2() : String {
    return "unimplemented"
}

private fun day41() {
    val data = getInput(4)
    val intReg = Regex("[0-9]+")
    val events = data.split("\n").map { str ->
        val ints = intReg.findAll(str).map { it.value.toInt() }.toList()
        val id = ints.getOrNull(5)
        Pair(Date(ints[1], ints[2], ints[3], ints[4]), id)
    }.sortedBy { it.first }

    val idSleepMap = mutableMapOf<Int, MutableList<Int>>()
    val iter = events.listIterator()
    var id : Int? = null
    while (iter.hasNext()) {
        var event = iter.next()
        if (event.second != null) {
            id = event.second
            event = iter.next()
        }
        val startDate = event.first
        val endDate = iter.next().first
        addTime(startDate, endDate, idSleepMap.getOrPut(id ?: -1) { MutableList(60) { 0 } })
        }

    val sleepiest = idSleepMap.maxBy { it.value.sum() }
    val sleepiestId = sleepiest?.key?: -1
    val sleepiestMin = sleepiest?.value?.zip((0..60))?.maxBy { x -> x.first }?.second?:-1
    println("Id: " + sleepiestId.toString()
            + ", sleepiest minute: " + sleepiestMin.toString()
            + ". Ans = " + (sleepiestId * sleepiestMin).toString())

    val sleepiestPt2 = idSleepMap.maxBy { it.value.max()?:-1 }
    val sleepiestIdPt2 = sleepiestPt2?.key?: -1
    val sleepiestMinPt2 = sleepiestPt2?.value?.zip((0..60))?.maxBy { x -> x.first }?.second?:-1
    println("Id: " + sleepiestIdPt2.toString()
            + ", sleepiest minute: " + sleepiestMinPt2.toString()
            + ". Ans = " + (sleepiestIdPt2 * sleepiestMinPt2).toString())

    }

class Date constructor (private val month : Int, private val day : Int, private val hour : Int, val min: Int) : Comparable<Date> {
    override fun compareTo(other: Date): Int {
        val month = this.month - other.month
        if (month != 0) return month
        val day = this.day - other.day
        if (day != 0) return day
        val hour = this.hour - other.hour
        if (hour != 0) return hour
        return this.min - other.min
    }

    override fun toString(): String {
        return month.toString() + "-" + day.toString() + " " + hour.toString() + ":" + min.toString()
    }
}

fun addTime (start : Date, end : Date, sleep : MutableList<Int>) {
    (start.min..end.min)
            .forEach { i ->
                sleep[i]++
            }
}