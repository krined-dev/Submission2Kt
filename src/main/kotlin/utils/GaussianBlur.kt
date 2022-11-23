package utils

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * [calcKernelValue]
 * Applies the gaussian function to a value given the calculated std deviation
 * This is the gaussian function in one dimension from: https://en.wikipedia.org/wiki/Gaussian_blur
 */
fun calcKernelValue(sigma: Double, dist: Double = 0.5): Double =
    ((2 * Math.PI).sqrt() * sigma).reciprocal() * (-dist.square() / (2 * sigma.square())).exponential()

fun gaussianKernel(radius: Int): List<Double> {
    val notN = (0 until radius * 2 + 1).map {
        calcKernelValue(radius / 3.0, (it - radius).toDouble().square()) }
    val sum = notN.sum()
    return notN.map { it / sum }
}

fun applyGaussian(img: BufferedImage, radius: Int): BufferedImage {
    val kernel = gaussianKernel(radius)
    return apply1DKernelToImage(apply1DKernelToImage(img, kernel, true), kernel, false)
}

fun BufferedImage.blur(radius: Int): BufferedImage {
    val kernel = gaussianKernel(radius)
    val vertical = apply1DKernelToImage(this, kernel, true)
    return apply1DKernelToImage(vertical, kernel, false)
}

// Lifted from https://stackoverflow.com/questions/9417356/bufferedimage-resize
fun BufferedImage.resize(newW: Int, newH: Int): BufferedImage {
    val tmp = this.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)
    val dImg = BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB)
    val g2d = dImg.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return dImg
}


/**
 * [apply1DKernelToImage] Applies the one dimensional kernel to the image
 * either in the vertical or horizontal plane
 */
private fun apply1DKernelToImage(img: BufferedImage, kernel: List<Double>, vertical: Boolean): BufferedImage {
    val outImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
    for (x in 0 until img.width) {
        for (y in 0 until img.height) {
            val color = getColorFrom1DKernelApplication(img, kernel, x, y, vertical)
            outImg.setRGB(x, y, color.rgb)
        }
    }
    return outImg
}

private fun getColorFrom1DKernelApplication(img: BufferedImage, kernel: List<Double>, x: Int, y: Int, vertical: Boolean): Color =
    (-(kernel.size / 2)..(kernel.size / 2)).fold(RGBA(0.0,0.0,0.0,0.0)) { accColor, i ->
        val x1 = if (vertical) x else clamp(x + i, 0, img.width - 1)
        val y1 = if (vertical) clamp(y + i, 0, img.height - 1) else y
        val color = Color(img.getRGB(x1, y1), true)
        val weight = kernel[i + kernel.size / 2]
        RGBA(
            accColor.r + (color.red * weight).toInt(),
            accColor.g + (color.green * weight).toInt(),
            accColor.b + (color.blue * weight).toInt(),
            accColor.a + (color.alpha * weight).toInt()
        )
    }.toColor()

data class RGBA(
    val r: Double,
    val g: Double,
    val b: Double,
    val a:Double
) {
    fun toColor(): Color = Color(
        clamp(r, 0.0, 255.0).toInt(),
        clamp(g, 0.0, 255.0).toInt(),
        clamp(b, 0.0, 255.0).toInt(),
        clamp(a, 0.0, 255.0).toInt())
}

// Some simple extension functions for mathematical operations
fun <T: Comparable<T>> clamp(a: T, min: T, max: T): T {
    return if (a < min) {
        min
    } else if (a > max ) {
        max
    } else a
}

fun Double.reciprocal(): Double = 1/this
fun Double.exponential(): Double = E.pow(this)
fun Double.square(): Double = this * this
fun Double.sqrt(): Double = sqrt(this)

