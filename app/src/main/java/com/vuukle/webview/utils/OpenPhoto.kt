package com.vuukle.webview.utils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.vuukle.webview.MainActivity
import com.vuukle.webview.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

class OpenPhoto {
    private val FORMAT_TIME = "yyyyMMddHHmmss"
    private val FILE_EXTENSION = ".jpg"
    private val FILE_PROVIDER = "com.vuukle.webview.android.fileprovider"
    private val VUUKLE = "VUUKLE"
    var imageUri: Uri? = null
        private set

    @Throws(IOException::class)
    private fun getPictureFile(contex: Context): File {
        val timeStamp = SimpleDateFormat(FORMAT_TIME).format(Date())
        val pictureFile = VUUKLE + timeStamp
        val storageDir = contex.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(pictureFile, FILE_EXTENSION, storageDir)
    }

    fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>(context.getString(R.string.take_photo), context.getString(R.string.choose_from_gallery), context.getString(R.string.cancel))
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.choose_your_profile_picture))
        builder.setItems(options) { dialog: DialogInterface, item: Int ->
            when {
                options[item] == context.getString(R.string.take_photo) -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    var photo: File? = null
                    try {
                        photo = getPictureFile(context)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageUri = FileProvider.getUriForFile(
                            context,
                            FILE_PROVIDER,
                            photo!!)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            imageUri)
                    (context as MainActivity).startActivityForResult(intent, MainActivity.REQUEST_SELECT_FILE)
                }
                options[item] == context.getString(R.string.choose_from_gallery) -> {
                    try {
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        (context as MainActivity).startActivityForResult(pickPhoto, MainActivity.REQUEST_SELECT_FILE)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show()
                    }
                }
                options[item] == context.getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


}