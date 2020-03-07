package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color

/**
 * A class containing every function related to image processing.
 */
@Suppress("NAME_SHADOWING")
class Edit {

    /**
     * A function responsible for processing bitmap with single value of processing intensity.
     *
     * @param bitmap bitmap that will be processed
     * @param intensity float value indicating how intense processing should be
     * @return processed bitmap
     */
    fun makeBlack(bitmap: Bitmap, intensity: Float): Bitmap {
        val intensity = intensity * 765 / 100

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.indices.forEach {
            val red = Color.red(pixels[it])
            val green = Color.green(pixels[it])
            val blue = Color.blue(pixels[it])

            if (red + green + blue <= intensity) {
                pixels[it] = -16777214
            }
        }

        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return processed
    }

    /**
     * A function responsible for processing bitmap with three different values of intensity. Each
     * indcating intensity of a different primary color (RGB).
     *
     * @param bitmap bitmap that will be processed
     * @param intensityRed float value indicating intensity of red color
     * @param intensityGreen float value indicating intensity of green color
     * @param intensityBlue float value indicating intensity of blue color
     * @return processed bitmap
     */
    fun makeBlack(
        bitmap: Bitmap,
        intensityRed: Float,
        intensityGreen: Float,
        intensityBlue: Float
    ): Bitmap {
        val intensityRed = intensityRed * 765 / 100
        val intensityGreen = intensityGreen * 765 / 100
        val intensityBlue = intensityBlue * 765 / 100

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.indices.forEach {
            val red = Color.red(pixels[it])
            val green = Color.green(pixels[it])
            val blue = Color.blue(pixels[it])

            if (red <= intensityRed && green <= intensityGreen && blue <= intensityBlue) {
                pixels[it] = -16777214
            }
        }

        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return processed
    }
}