package utils

import arrow.core.Either
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.io.path.Path


/**
 * [Png] Simple newtype for BufferedImage where we validate on the file byte header
 * This isn't really needed, but I wanted to try it
 */
@JvmInline
value class Png(val image: BufferedImage) {

    /**
     * [resize] Returns [Pair] (newWidth, newHeight) with correct ratio
     */

    fun createPyramid(iterations: Int, radius: Int): List<BufferedImage> {
        var img = this.image
        return (0 until iterations).map {
            val image = applyGaussian(img, img.height / 2, img.width / 2, radius)
            img = image
            image
        }
    }





    /**
     * [printPixels] Simple function for printing the RGBA representation of a pixel
     */
    fun printPixels() {
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

}

fun getPngFromPath(path: String): Either<ValidationError, Png> {
    val imageBytes = runCatching { Files.readAllBytes(Path(path)) }.getOrElse {
        return Either.Left(ValidationError.UnableToReadFile(it.stackTraceToString()))
    }

    return imageBytes.validatePng()


}

fun ByteArray.validatePng():  Either<ValidationError, Png> {
    if (this.size < 8) return Either.Left(ValidationError.PngFileTooSmall)

    val pngHeader = byteArrayOf(
        137.toByte(),
        80.toByte(),
        78.toByte(),
        71.toByte(),
        13.toByte(),
        10.toByte()
        ,26.toByte()
        ,10.toByte()
    )

    val validHeader = this.take(8).filterIndexed { index, byte ->
        byte == pngHeader[index]
    }

    val imageBuffer = runCatching { ImageIO.read(ByteArrayInputStream(this)) }
        .getOrElse { return Either.Left(ValidationError.ValidatePngBytesError(it.stackTraceToString())) }

    return if (validHeader.size == 8) {
        Either.Right(Png(imageBuffer))
    } else {
        Either.Left(ValidationError.InvalidPngHeader)
    }
}

sealed class ValidationError(private val errorMsg: String) {
    data class UnableToReadFile(val stackTrace: String):
        ValidationError("Could not get file from given path:\n$stackTrace") {
        override fun toString(): String  = super.toString() }

    object PngFileTooSmall: ValidationError("Not enough bytes to contain valid PNG header") {
        override fun toString(): String = super.toString() }

    object InvalidPngHeader: ValidationError("Invalid PNG header in file, wrong file format") {
        override fun toString(): String = super.toString() }

    data class ValidatePngBytesError(val stackTrace: String): ValidationError("Error when validating PNG:\n$stackTrace") {
        override fun toString(): String = super.toString() }

    override fun toString(): String = "${this::class.java.canonicalName}: ${this.errorMsg}"
}


