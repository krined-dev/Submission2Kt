package utils

import arrow.core.Either
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.io.path.Path

@JvmInline
value class Png(val image: BufferedImage)

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


