import utils.clamp
import utils.gaussianKernel
import utils.getPngFromPath

fun main() {
    val png = getPngFromPath("/home/kristian/src/Submission2/src/main/resources/harnverhalt4.png")

    //png.orNull()!!.printPixels()

    println(gaussianKernel(2))
    println( clamp(10, 11, 20)) // prints 11
    println(clamp(10, 1, 9)) // prints 9
    println(clamp(10, 1, 11)) // prints 10
}


