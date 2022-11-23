import utils.*
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {

    if (args[0] == "pyramid") {
        val png = getPngFromPath("/home/kristian/src/Submission2/src/main/resources/harnverhalt4.png").orNull()!!

        val blur = png.image.blur(2)
        ImageIO.write(blur, "PNG", File("/home/kristian/src/Submission2/src/main/resources/harnverhalt4BLUR.png"))

        // start timer
        val start = System.currentTimeMillis()
        val pyramid = png.createPyramid(5, 10)
        // stop timer
        val end = System.currentTimeMillis()
        println("Time: ${end - start}ms")



        pyramid.forEachIndexed { idx, img ->
            ImageIO.write(
                img,
                "PNG",
                File("/home/kristian/src/Submission2/src/main/resources/harnverhalt4BLUR_$idx.png")
            )
        }
    }

    if (args[0] == "pixels") {
        val png = getPngFromPath("/home/kristian/src/Submission2/src/main/resources/harnverhalt4.png").orNull()!!
        png.printPixels()
    }
}


