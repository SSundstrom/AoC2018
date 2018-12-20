package days
import input.*
import util.Pos

fun day15(pt: Int): String {
    when (1) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(50) + pt2()
}

private fun pt1() : String {
    return "unimplemented"
}

private fun pt2() : String {
    return "unimplemented"
}

private class Soldier constructor(pos : Pos)