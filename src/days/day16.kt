package days
import input.*

fun day16(pt: Int): String {
    when (pt) {
        1 -> return pt1()
        2 -> return pt2()
    }
    return pt1().padEnd(40) + pt2()
}

private fun pt1() : String {
//    parse input
    val inputIterator = getInput(16).lines().iterator()
    val tests = mutableListOf<OpTest>()
    var line = inputIterator.next()
    while (line.startsWith("Before")) {
        val preReg = parseTestLine(line)
        line = inputIterator.next()
        val op = line.split(' ').map { it.toInt() }
        line = inputIterator.next()
        val postReg = parseTestLine(line)
        tests.add(OpTest(op[0], op[1], op[2], op[3], preReg, postReg))
        do {
            line = inputIterator.next()
        } while (line.isEmpty())
    }
//    for each test try and use the different operations
    val tmp = tests.map { it.possibleOps().size }
//    check which have >3 codes that work
    val threeOrMore = tmp.count { it >= 3 }
//    return that count
    return "Pt1 : $threeOrMore"
}

private fun pt2() : String {
//    Read and parse input
    val inputIterator = getInput(16).lines().iterator()
    val tests = mutableListOf<OpTest>()
    var line = inputIterator.next()
    while (line.startsWith("Before")) {
        val preReg = parseTestLine(line)
        line = inputIterator.next()
        val op = line.split(' ').map { it.toInt() }
        line = inputIterator.next()
        val postReg = parseTestLine(line)
        tests.add(OpTest(op[0], op[1], op[2], op[3], preReg, postReg))
        do {
            line = inputIterator.next()
        } while (line.isEmpty())
    }
    val operations = mutableListOf(line.split(" ").map { it.toInt() })
    while (inputIterator.hasNext()) {
        operations.add(inputIterator.next().split(" ").map { it.toInt() })
    }
//    Figure out which Op nr is which.
    var ops = mutableMapOf<Int, List<String>>()
    val confOps = mutableMapOf<Int, String>()
    tests.forEach { test ->
        val working = test.possibleOps()
        val id = test.op
        if (id in ops) {
            ops[id] = ops[id]?.filter { it in working }?: error("$id not in ${ops.keys}")
        } else {
            if (working.size == 1) {
                confOps[id] = working.first()
            } else {
                ops[id] = working
            }
        }
    }
    while (ops.isNotEmpty()) {
        confOps.forEach { _, func ->
            ops.forEach { code, funId -> ops[code] = funId.filter { it != func } }
        }
        ops.forEach {if (it.value.size == 1) confOps[it.key] = it.value.first()}
        ops = ops.filter { it.value.size != 1 }.toMutableMap()
    }
    if (ops.any { it.value.size != 1 }) error("More than 1 op possible for a given code: $ops")


//    Use those settings to read each line
    val reg = IntArray(4)
    operations.forEach {
//        [Op, A, B, C]
//        [0 , 1, 2, 3]
        reg[it[3]] = functionsMap[confOps[it[0]]]?.invoke(it[1], it[2], reg)?: error("error in operation read\n$it")
    }
    return "Pt2: ${reg[0]}"
}

private fun parseTestLine(line : String) : IntArray {
    return Regex("[0-9]").findAll(line).map { it.value.toInt() }.toList().toIntArray()
}

private class OpTest constructor(val op : Int, val A : Int, val B : Int, val C : Int, val preReg : IntArray, val postReg : IntArray) {
    fun possibleOps() : List<String> {
        val workingOps = mutableListOf<String>()

        functionsMap.forEach { id, func ->
            val resReg = preReg.clone()
            resReg[C] = func(A, B, preReg)
            if (resReg.contentEquals(postReg)) {
                workingOps.add(id)
            }
        }

        return workingOps
    }
}


private val functionsMap = mapOf<String, (Int, Int, IntArray) -> Int>(
        "addr" to {a, b, reg -> reg[a] + reg[b]},
        "addi" to {a, b, reg -> reg[a] + b},
        "mulr" to {a, b, reg -> reg[a] * reg[b]},
        "muli" to {a, b, reg -> reg[a] * b},
        "banr" to {a, b, reg -> reg[a] and reg[b]},
        "bani" to {a, b, reg -> reg[a] and b},
        "borr" to {a, b, reg -> reg[a] or reg[b]},
        "bori" to {a, b, reg -> reg[a] or b},
        "setr" to {a, b, reg -> reg[a]},
        "seti" to {a, b, reg -> a},
        "gtir" to {a, b, reg -> if (a > reg[b]) 1 else 0},
        "gtri" to {a, b, reg -> if (reg[a] > b) 1 else 0},
        "gtrr" to {a, b, reg -> if (reg[a] > reg[b]) 1 else 0},
        "eqir" to {a, b, reg -> if (a == reg[b]) 1 else 0},
        "eqri" to {a, b, reg -> if (reg[a] == b) 1 else 0},
        "eqrr" to {a, b, reg -> if (reg[a] == reg[b]) 1 else 0}
        )