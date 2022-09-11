import utils.Png
import utils.getPngFromPath
import java.awt.Color

fun main() {
    val png = getPngFromPath("/home/kristian/src/Submission2/src/main/resources/harnverhalt4.png")

    png.orNull()!!.printPixels()
}

fun Png.printPixels() {
    val width = this.image.width
    val height = this.image.width
    // Interop with java that might be null - comparable to Java optional

    (0 until height).forEach { h ->
        (0 until width).forEach { w ->
            val pixel = this.image.getRGB(w, h)
            val color = Color(pixel, true)
            println("R: ${color.red}, G: ${color.green}, B: ${color.blue}, A: ${color.alpha}")
        }
    }

}

