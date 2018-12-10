package days
import input.*

fun day8(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val list = getInput(8).split(" ").map { it.toInt() }
    val metaSum = collectMeta(list).first
    return "Pt1: " + metaSum.toString()
}

private fun collectMeta(list : List<Int>) : Pair<Int, List<Int>> {
    val children = list[0]
    val metaPoints = list[1]
    var metaSum = 0
    var updatedList = list.drop(2)

    (0..(children-1)).forEach {
        val pair = collectMeta(updatedList)
        metaSum += pair.first
        updatedList = pair.second
    }

    (0..(metaPoints-1)).forEach {
        metaSum += updatedList[it]
    }

    return Pair(metaSum, updatedList.drop(metaPoints))
}

private fun pt2() : String {
    val list = getInput(8).split(" ").map { it.toInt() }
    val metaSum = collectMetaPt2(list).first
    return "Pt1: " + metaSum.toString()
}

private fun collectMetaPt2(list : List<Int>) : Pair<Int, List<Int>> {
    val children = list[0]
    val metaPoints = list[1]
    var metaSum = 0
    var updatedList = list.drop(2)

    val childrenMap = (1..(children)).map {
        val pair = collectMetaPt2(updatedList)
        updatedList = pair.second
        Pair(it, pair.first)
    }.toMap()

    (0..(metaPoints-1)).forEach {
        metaSum += if (children > 0) {
            childrenMap.getOrDefault(updatedList[it], 0)
        } else {
            updatedList[it]
        }
    }

    return Pair(metaSum, updatedList.drop(metaPoints))
}



class Node constructor(val id: Int, val children : Int, val meta : Int)
