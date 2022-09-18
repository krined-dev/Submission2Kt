package utils

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.E
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * [calcKernelValue]
 * Applies the gaussian function to a value given the calculated std deviation
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
    val temp = applyVertical(img, kernel)
    return applyHorizontal(temp, kernel)
}

fun BufferedImage.blur(radius: Int): BufferedImage {
    val kernel = gaussianKernel(radius)
    val temp = applyVertical(this, kernel)
    return applyHorizontal(temp, kernel)
}

// A bit hacky way to resize for now
fun BufferedImage.resize(newW: Int, newH: Int): BufferedImage {
    val tmp = this.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)
    val dimg = BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB)
    val g2d = dimg.createGraphics()
    g2d.drawImage(tmp, 0, 0, null)
    g2d.dispose()
    return dimg
}

/**
 * [applyHorizontal] One dimensional convolutional filter that applies the given kernel in the plane
 */
private fun applyHorizontal(img: BufferedImage, kernel: List<Double>): BufferedImage {
    val height = img.height
    val width = img.width
    val outImg = BufferedImage(width, height, img.type)

    (kernel.size until (width - kernel.size)).forEach { x ->

        (kernel.size until (height - kernel.size)).forEach { y ->
            // RED, GREEN, BLUE, ALPHA values
            val p = mutableListOf(0.0, 0.0, 0.0, 0.0)

            kernel.forEachIndexed { idx, kVal ->
                val xTemp = (x - kernel.size) + idx // Just a hacky way to deal with edges
                val xCoord = if (xTemp < 0) 0 else xTemp
                val channels = Color(img.getRGB(xCoord, y), true)
                //println("left $left idx $idx")
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

private fun applyVertical(img: BufferedImage, kernel: List<Double>): BufferedImage {
    val width = img.width
    val height = img.height
    val outImg = BufferedImage(width, height, img.type)

    (kernel.size until (height - kernel.size)).forEach { y ->

        ((kernel.size) until (width - kernel.size)).forEach { x ->
            // RED, GREEN, BLUE, ALPHA values
            val p = mutableListOf(0.0, 0.0, 0.0, 0.0)

            kernel.forEachIndexed { idx, kVal ->
                val yTemp = (y - kernel.size) + idx
                val yCoord = if (yTemp < 0) 0 else yTemp
                val channels = Color(img.getRGB(x, yCoord), true)
                p[0] += channels.red * kVal
                p[1] += channels.green * kVal
                p[2] += channels.blue * kVal
                p[3] += channels.alpha * kVal
            }

            //println("${p[0]},${p[1]}, ${p[2]}, ${p[3]}")
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

    val newWidth = (width * ratio).toInt()
    val newHeight = (width * ratio).toInt()

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
