package com.sanisamoj.services.linkEntry

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object QrCode {
    fun generate(text: String, width: Int, height: Int): ByteArray {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height
        val image = BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until matrixWidth) {
            for (y in 0 until matrixHeight) {
                image.setRGB(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        val scaledImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = scaledImage.createGraphics()
        graphics.drawImage(image, 0, 0, width, height, null)
        graphics.dispose()

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(scaledImage, "PNG", outputStream)
        return outputStream.toByteArray()
    }
}