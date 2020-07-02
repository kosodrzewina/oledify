package com.kosodrzewinatru.oledify

import android.graphics.Bitmap

class GalleryItem {
    var bitmap0: Bitmap
    var bitmap1: Bitmap

    constructor(bitmap0: Bitmap, bitmap1: Bitmap) {
        this.bitmap0 = bitmap0
        this.bitmap1 = bitmap1
    }

    constructor(bitmap: Bitmap) {
        bitmap0 = bitmap
        bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}