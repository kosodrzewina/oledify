package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

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
                pixels[it] = Color.BLACK
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
        val intensityRed = intensityRed * 255 / 100
        val intensityGreen = intensityGreen * 255 / 100
        val intensityBlue = intensityBlue * 255 / 100

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.indices.forEach {
            val red = Color.red(pixels[it])
            val green = Color.green(pixels[it])
            val blue = Color.blue(pixels[it])

            if (red <= intensityRed && green <= intensityGreen && blue <= intensityBlue) {
                pixels[it] = Color.BLACK
            }
        }

        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return processed
    }

    fun makeBlackToneCurve(bitmap: Bitmap, intensity: Float): Bitmap {
        val topRange = intensity * 765 / 100
        val bottomRange = topRange - 300

        val pixels = IntArray(bitmap.height * bitmap.width)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.indices.forEach {
            val red = Color.red(pixels[it])
            val green = Color.green(pixels[it])
            val blue = Color.blue(pixels[it])
            val totalValue = red + green + blue

            if (totalValue < (bottomRange + (topRange - bottomRange) / 2)) {
                pixels[it] = Color.BLACK
            } else if (totalValue <= topRange) {
                val degradationRate = totalValue / topRange

                val newRed = (red * degradationRate).toInt()
                val newGreen = (green * degradationRate).toInt()
                val newBlue = (blue * degradationRate).toInt()

                pixels[it] = Color.rgb(
                    newRed,
                    newGreen,
                    newBlue
                )
            }
        }

        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return processed
    }


    fun makeBlackToneCurve(
        bitmap: Bitmap,
        intensityRed: Float,
        intensityGreen: Float,
        intensityBlue: Float
    ): Bitmap {
        val intensityRed = intensityRed * 765 / 100
        val intensityGreen = intensityGreen * 765 / 100
        val intensityBlue = intensityBlue * 765 / 100

        val topRangeRed = intensityRed * 765 / 100
        val bottomRangeRed = topRangeRed - 300
        val topRangeGreen = intensityGreen * 765 / 100
        val bottomRangeGreen = topRangeGreen - 300
        val topRangeBlue = intensityBlue * 765 / 100
        val bottomRangeBlue = topRangeBlue - 300

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.indices.forEach {
            val red = Color.red(pixels[it])
            val green = Color.green(pixels[it])
            val blue = Color.blue(pixels[it])

            if (
                red < (bottomRangeRed + (topRangeRed - bottomRangeRed) / 2)
                && green < (bottomRangeGreen + (topRangeGreen - bottomRangeGreen) / 2)
                && blue < (bottomRangeBlue + (topRangeBlue - bottomRangeBlue) / 2)
            ) {
                pixels[it] = Color.BLACK
            } else if (red <= topRangeRed && green <= topRangeGreen && blue <= topRangeBlue) {
                val degradationRate =
                    (red + green + blue) / (topRangeRed + topRangeGreen + topRangeBlue)

                val newRed = (red * degradationRate).toInt()
                val newGreen = (green * degradationRate).toInt()
                val newBlue = (blue * degradationRate).toInt()


                pixels[it] = Color.rgb(
                    newRed,
                    newGreen,
                    newBlue
                )
            }
        }


        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return processed
    }
}