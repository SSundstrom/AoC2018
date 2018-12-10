package input

import java.io.File

fun getInput(day: Int): String {
    val dir = System.getProperty("user.dir")
    return File("$dir/inputFiles/day$day").readText()
}

fun getInputT(day: Int): String {
    val dir = System.getProperty("user.dir")
    return File("$dir/inputFiles/day$day" + "test").readText()
}