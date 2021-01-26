import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.R
import kotlinx.android.synthetic.main.fragment_image_preview.*
import java.io.File

class ImagePreviewFragment(private val path: String) : DialogFragment() {
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
            val file = File(path)
            val intent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse(file.absolutePath)

            intent.setDataAndType(data, "image/*")
            startActivity(intent)
        }

        delete_button.setOnClickListener {}
    }
}