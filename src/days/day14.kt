package days

fun day14(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(40) + pt2()
}

private fun pt1() : String {
    val input = 540561
    var tail = Recipe(3)
    tail = tail.newRecipeAfter(7)
    var elf1 = tail.prev
    var elf2 = tail
    var recipeCount = 2
    while (recipeCount < input + 10) {
        val res = elf1.score + elf2.score
        if (res > 9) {
            tail = tail.newRecipeAfter(res/10)
            tail = tail.newRecipeAfter(res%10)
            recipeCount++
        } else {
            tail = tail.newRecipeAfter(res)
        }
        recipeCount++
        elf1 = elf1.move()
        elf2 = elf2.move()
    }

    val solution = IntArray(10)
    if (recipeCount > input + 10) {
        tail = tail.prev
    }

    for (i in 0 until 10) {
        solution[9-i] = tail.score
        tail = tail.prev
    }
    return solution.joinToString("")
}

private fun pt2() : String {
    val input = listOf(5, 4, 0, 5, 6, 1)
    //val input = listOf(5,1,5,8,9) // 9
    //val input = listOf(0,1,2,4,5) //5
    //val input = listOf(5,9,4,1,4) // 2018
    //val input = listOf(5,1,3,4,0,1) // 20286858
    var tail = Recipe(3)
    tail = tail.newRecipeAfter(7)
    var elf1 = tail.prev
    var elf2 = tail
    var recipeCount = 2
    val solutions = mutableListOf(0, 0, 0, 0, 3, 7)
    var foundSequence = false
    while (!foundSequence) {
        val res = elf1.score + elf2.score
        val singles = res%10
        if (res > 9) {
            val tens = res/10
            tail = tail.newRecipeAfter(tens)
            solutions.add(tens); solutions.removeAt(0)
            foundSequence = input == solutions
            recipeCount++
        }
        tail = tail.newRecipeAfter(singles)
        solutions.add(singles); solutions.removeAt(0)
        if (!foundSequence) {
            foundSequence = input == solutions
        } else {
            recipeCount--
        }
        recipeCount++

        elf1 = elf1.move()
        elf2 = elf2.move()
    }

    val solution = IntArray(11)

    for (i in 0 until 11) {
        solution[10-i] = tail.score
        tail = tail.prev
    }

    return "Pt2: ${recipeCount - input.size}"
}

private class Recipe (val score : Int) {
    constructor(score : Int, prev : Recipe, next : Recipe, head : Recipe) : this(score) {
        this.prev = prev
        this.next = next
        this.head = head
        if (score > 9) {
            println("fault: $score")
        }
    }
    var next = this
    var prev = this
    var head = this

    fun newRecipeAfter(score : Int): Recipe {
        val retVal = Recipe(score, this, next, head)
        next.prev = retVal
        next = retVal
        return retVal
    }

    fun move() : Recipe {
        var retVal = this
        for (i in 0 until score + 1) {
            retVal = retVal.next
        }
        return retVal
    }

    override fun toString(): String {
        var retString = "${head.score}"
        var curr = head.next
        while (curr != head) {
            retString += "${curr.score}"
            curr = curr.next
        }
        return retString
    }

}