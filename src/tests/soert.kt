package tests

fun sorting(): Unit {
    val a = mutableListOf<Int>()
    a.add(5)
    a.add(3)
    a.add(9)
    a.add(1)
    println(a)
    a.sort()
    println(a)
}

fun mapArrays() : Unit {
    val a = IntArray(50, {it})
    val b = IntArray(20)
    val c = IntArray(50, {it})
    val d = IntArray(50, {50-it})

    val map = mutableMapOf<IntArray, Int>(Pair(a, 1), Pair(b, 2), Pair(d, 3))
    println(map.containsKey(c))

    val a1 = "asdasdasd"
    val b1 = "asdasdasd"
    val map2 = mutableMapOf<String, Int>(Pair(a1, 1))
    println(b1 in map2)

    val a2 = Pair("asdasdasd", 1)
    val b2 = Pair("asdasdasd", 1)
    val map3 = mutableMapOf<Pair<String, Int>, Int>(Pair(a2, 1))
    println(b2 in map3)

}

fun listInlistEq(): Unit {
    val c = (1..5).toMutableList()
    val d = (1..3).toMutableList()
    val a = mutableListOf<MutableList<Int>>(c, d)
    val b = (1..5).toList()
    println(b in a)
}