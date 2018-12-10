package days

fun day9(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1() + "\t" + pt2()
}

private fun pt1() : String {
    val lastMarble = 71032
    val players = 441

    val finalScore = playGame(players, lastMarble)

    return "Pt1: " + finalScore.toList().map { it.second }.max()

}


private fun pt2() : String {
    val lastMarble = 7103200
    val players = 441

    val finalScore = playGame(players, lastMarble)

    return "Pt2: " + finalScore.toList().map { it.second }.max()

}

private fun playTurn(value : Int, marble: Marble): Pair<Int, Marble> {
    var score = 0
    var current = marble
    if (value % 23 == 0) {
        score = value
        current = current.sevenCounter()
        score += current.value
        current = current.remove()
    } else {
        current = current.clockMarble
        current = current.insertClockwise(value)
    }
    return Pair(score, current)
}

private fun playGame(players : Int, lastMarble : Int ) : MutableMap<Int, Long> {
    var currentValue = 1
    var elf = 0
    var currentMarble = Marble(0)
    val scoreMap = mutableMapOf<Int, Long>()

    while (currentValue <= lastMarble) {
        val scoreAndCurrent = playTurn(currentValue++, currentMarble)
        currentMarble = scoreAndCurrent.second
        if (scoreAndCurrent.first > 0) {
            scoreMap.put(elf, scoreMap.getOrDefault(elf, 0) + scoreAndCurrent.first)
        }
        elf = (elf  + 1) % players
    }

    return scoreMap
}


private class Marble constructor(val value : Int) {

    constructor(value : Int, clockMarble : Marble, counterMarble : Marble) : this(value) {
        this.clockMarble = clockMarble
        this.counterMarble = counterMarble
    }

    var clockMarble : Marble = this
    var counterMarble : Marble = this

    fun insertClockwise(newValue: Int) : Marble {
        val new = Marble(newValue, clockMarble, this)
        clockMarble.counterMarble = new
        clockMarble = new
        return new
    }

    fun remove() : Marble {
        counterMarble.clockMarble = clockMarble
        clockMarble.counterMarble = counterMarble
        return clockMarble
    }

    fun sevenCounter() : Marble {
        var current = this
        (1..7).forEach {
            current = current.counterMarble
        }
        return current
    }

    override fun toString(): String {
        return clockMarble.value.toString() + " - ( " + value.toString() + " ) - " + counterMarble.value.toString()
    }
}