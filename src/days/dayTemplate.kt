package days
import input.*

fun day(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(40) + pt2()
}

private fun pt1() : String {
    return "unimplemented"
}

private fun pt2() : String {
    return "unimplemented"
}