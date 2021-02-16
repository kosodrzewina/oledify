import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.GalleryFragment
import kotlinx.android.synthetic.main.fragment_image_preview.*
import java.io.File

class ImagePreviewFragment(private val bitmap: Bitmap, private val path: String) :
    DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_preview, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        image.setImageDrawable(Drawable.createFromPath(path))

        open_button.setOnClickListener {
            WallpaperManager.getInstance(context).setBitmap(bitmap)
            Toast.makeText(context, R.string.wallpaper_set, Toast.LENGTH_SHORT).show()
        }

        delete_button.setOnClickListener {
            val file = File(path)

            GalleryFragment.adapter.removeItem(path, GalleryFragment.recyclerView)
            file.delete()
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.toString()),
                arrayOf(file.name),
                null
            )
            this.dismiss()
        }
    }
}
