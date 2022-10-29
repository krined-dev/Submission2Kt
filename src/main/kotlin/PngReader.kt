import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utils.blur
import utils.clamp
import utils.gaussianKernel
import utils.getPngFromPath
import java.io.File
import javax.imageio.ImageIO

suspend fun main() {
    val png = getPngFromPath("/home/kristian/src/Submission2Kt/src/main/resources/harnverhalt4.png").orNull()!!

    // start timer
    val start = System.currentTimeMillis()
    val pyramid = png.createPyramid(5, 5)
    // stop timer
    val end = System.currentTimeMillis()
    println("Time: ${end - start}ms")

    pyramid.forEachIndexed { idx, img ->
        ImageIO.write(img, "PNG", File("/home/kristian/src/Submission2Kt/src/main/resources/harnverhalt4BLUR_$idx.png"))
    }

    println(gaussianKernel(2))
    println( clamp(10, 11, 20)) // prints 11
    println(clamp(10, 1, 9)) // prints 9
    println(clamp(10, 1, 11)) // prints 10
}


