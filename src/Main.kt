import days.*
import kotlin.system.measureTimeMillis
import java.time.LocalDateTime


fun main(args: Array<String>) {

    if (false) {
        runAll()
    } else {
        runLatest()
    }
}

fun t1() {
    var t1 = 0.toLong()
    var t2 = 0.toLong()
    val index = 5
    for (i in 10 .. 20000 ) {
        t1 += measureTimeMillis {
            BooleanArray(500, {false}).any()
        }
        t2 += measureTimeMillis {
            val a = BooleanArray(500)
            a.any()
        }
    }
    println("$t1 t1\n$t2 t2")
}

fun runLatest() {
    val day = LocalDateTime.now().dayOfMonth
    runDayTimed(day)
}

fun runAll() {
    (1..LocalDateTime.now().dayOfMonth).forEach { runDayTimed(it) }
}

fun runDayTimed(day : Int) {
    var res = ""
    val time = measureTimeMillis { res = runDayPt(day, 0) }
    println("Day " + day.toString() + " done in " + time.toString() + "ms")
    println(res)
}

fun runDayPt(day: Int, part : Int?): String {
    val pt = part?:0
    return when (day) {
        1 -> day1(pt)
        2 -> day2(pt)
        3 -> day3(pt)
        4 -> day4(pt)      
        5 -> day5(pt)
        6 -> day6(pt)
        7 -> day7(pt)
        8 -> day8(pt)
        9 -> day9(pt)
        10 -> day10(pt)
        11 -> day11(pt)
        12 -> day12(pt)/*
        13 -> day13()
        14 -> day14()
        15 -> day15()
        16 -> day16()
        17 -> day17()
        18 -> day18()
        19 -> day19()
        20 -> day20()
        21 -> day21()
        22 -> day22()
        23 -> day23()
        24 -> day24()                   */
        else -> "fault"
    }
}