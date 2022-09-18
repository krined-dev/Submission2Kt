import utils.blur
import utils.clamp
import utils.gaussianKernel
import utils.getPngFromPath
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val png = getPngFromPath("/home/kristian/src/Submission2/src/main/resources/harnverhalt4.png")

    //png.orNull()!!.printPixels()

    val img = png.orNull()!!.image.blur()

    ImageIO.write(img, "PNG", File("/home/kristian/src/Submission2/src/main/resources/harnverhalt4BLUR.png"))

    println(gaussianKernel(2))
    println( clamp(10, 11, 20)) // prints 11
    println(clamp(10, 1, 9)) // prints 9
    println(clamp(10, 1, 11)) // prints 10
}


