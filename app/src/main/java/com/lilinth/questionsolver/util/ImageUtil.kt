package com.lilinth.questionsolver.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

/**
 * 图片处理工具类
 */
object ImageUtil {
    private const val MAX_IMAGE_SIZE = 2048
    private const val JPEG_QUALITY = 85

    /**
     * 从 Uri 读取并压缩图片
     */
    fun compressImage(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_SIZE)
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            val bytes = outputStream.toByteArray()

            resizedBitmap.recycle()
            if (resizedBitmap !== originalBitmap) {
                originalBitmap.recycle()
            }

            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 按比例缩放图片
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val scale = maxSize.toFloat() / maxOf(width, height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
