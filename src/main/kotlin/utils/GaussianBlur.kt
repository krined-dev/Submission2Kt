package utils

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.E
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * [calcKernelValue]
 * Applies the gaussian function to a value given the calculated std deviation
 */
fun calcKernelValue(sigma: Double, dist: Double): Double =
    ((2 * Math.PI).sqrt() * sigma).reciprocal() * (-dist.square() / (2 * sigma.square())).exponential()

fun gaussianKernel(radius: Int): List<Double> {
    val notN = (0 until radius * 2 + 1).map {
        calcKernelValue(radius / 3.0, (it - radius).toDouble().square()) }
    val sum = notN.sum()
    return notN.map { it / sum }
}

fun applyGaussian(img: BufferedImage, nHeight: Int, nWidth: Int, radius: Int): BufferedImage {
    val dimens = resize(img.width, nWidth, img.height, nHeight)
    val kernel = gaussianKernel(radius)
    val temp = applyVertical(img, dimens.second, kernel)
    return applyHorizontal(temp, dimens.first, kernel)
}



/**
 * [applyHorizontal] One dimensional convolutional filter that applies the given kernel in the horizontal plane
 */
private fun applyHorizontal(img: BufferedImage, nWidth: Int, kernel: List<Double>): BufferedImage {
    val width = img.width
    val height = img.height
    val outImg = BufferedImage(nWidth, height, img.type)
    val ratio = width.toDouble() / nWidth
    val support = kernel.size * ratio

    (0 until nWidth).forEach { x ->
        val inX = (x.toDouble() + 0.5) * ratio

        val left = clamp((inX - support).floor().toLong(), 0, (width - 1).toLong()).toInt()

        (0 until height).forEach { y ->
            // RED, GREEN, BLUE, ALPHA values
            val p = mutableListOf(0.0, 0.0, 0.0, 0.0)

            kernel.forEachIndexed { idx, kVal ->
                val channels = Color(img.getRGB(left + idx, y), true)
                p[0] += channels.red * kVal
                p[1] += channels.green * kVal
                p[2] += channels.blue * kVal
                p[3] += channels.alpha * kVal
            }

            val pixelChannels = Color(
                p[0].toInt(),
                p[1].toInt(),
                p[2].toInt(),
                p[3].toInt()
            )

            outImg.setRGB(x, y, pixelChannels.rgb)

        }
    }
    return outImg
}

private fun applyVertical(img: BufferedImage, nHeight: Int, kernel: List<Double>): BufferedImage {
    val width = img.width
    val height = img.height
    val outImg = BufferedImage(width, nHeight, img.type)
    val ratio = height.toDouble() / nHeight
    val support = kernel.size * ratio

    (0 until nHeight).forEach { y ->
        val inY = (y.toDouble() + 0.5) * ratio

        val left = clamp((inY - support).floor().toLong(), 0, (width - 1).toLong()).toInt()



        (0 until width).forEach { x ->
            // RED, GREEN, BLUE, ALPHA values
            val p = mutableListOf(0.0, 0.0, 0.0, 0.0)

            kernel.forEachIndexed { idx, kVal ->
                val channels = Color(img.getRGB(x, left + idx), true)
                p[0] += channels.red * kVal
                p[1] += channels.green * kVal
                p[2] += channels.blue * kVal
                p[3] += channels.alpha * kVal
            }

            val pixelChannels = Color(
                p[0].toInt(),
                p[1].toInt(),
                p[2].toInt(),
                p[3].toInt()
            )

            outImg.setRGB(x, y, pixelChannels.rgb)

        }
    }
    return outImg
}

// Utilities - Just some utils that makes math easier
private fun resize(width: Int, nWidth: Int, height:Int,  nHeight: Int): Pair<Int, Int> {
    val heightRatio =  (nHeight* 1.0) / height
    val widthRatio =  (nWidth* 1.0) / width

    val ratio = min(heightRatio, widthRatio)

    val newWidth = (nWidth * ratio).toInt()
    val newHeight = (nHeight* ratio).toInt()

    return Pair(newWidth, newHeight)
}

fun <T: Comparable<T>> clamp(a: T, min: T, max: T): T {
    return if (a < min) {
        min
    } else if (a > max ) {
        max
    } else a
}

// Some simple extension functions for mathematical operations
// A bit strange that these aren't a part of the standard library.
fun Double.reciprocal(): Double = 1/this
fun Double.exponential(): Double = E.pow(this)
fun Double.square(): Double = this * this
fun Double.sqrt(): Double = sqrt(this)
fun Double.floor(): Double = kotlin.math.floor(this)
fun Double.ceil(): Double = kotlin.math.ceil(this)
