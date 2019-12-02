package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color

class Editing {

    // main function responsible for processing bitmap
    fun makeBlack(bitmap: Bitmap, intensity: Float): Bitmap {
        val intensity = intensity * 765 / 100

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        (pixels.indices).forEach {
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

    fun makeBlack(bitmap: Bitmap, intensityRed: Float, intensityGreen: Float, intensityBlue: Float): Bitmap {
        val intensityRed = intensityRed * 765 / 100
        val intensityGreen = intensityGreen * 765 / 100
        val intensityBlue = intensityBlue * 765 / 100

        val pixels = IntArray(bitmap.height * bitmap.width)

        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        (pixels.indices).forEach {
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