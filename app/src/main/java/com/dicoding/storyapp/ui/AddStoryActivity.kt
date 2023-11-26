package com.dicoding.storyapp.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.remote.retrofit.ApiClient
import com.dicoding.storyapp.utility.getImageUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString()

            if (description.isNotEmpty() && currentImageUri != null) {
                uploadImage(description, currentImageUri!!)
            } else {
                Toast.makeText(this,
                    getString(R.string.please_provide_description_and_select_an_image), Toast.LENGTH_SHORT).show()
            }
        }
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private suspend fun compressImage(uri: Uri, targetSizeInKB: Int): File {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        val outputStream = ByteArrayOutputStream()
        var quality = 100

        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10

        } while (outputStream.size() / 1024 > targetSizeInKB && quality > 0)

        val file = File(cacheDir, "compressed_image.jpg")
        val fileOutputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        withContext(Dispatchers.IO) {
            outputStream.writeTo(fileOutputStream)
        }

        withContext(Dispatchers.IO) {
            fileOutputStream.flush()
        }
        withContext(Dispatchers.IO) {
            fileOutputStream.close()
        }

        return file
    }


    private fun uploadImage(description: String, imageUri: Uri) {
        loadingProgressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val compressedImageFile = compressImage(imageUri,1000)
                val requestFile: RequestBody =
                    compressedImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val photoPart: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", compressedImageFile.name, requestFile)
                val descriptionRequestBody: RequestBody =
                    description.toRequestBody("text/plain".toMediaTypeOrNull())

                val apiService = ApiClient.createApiClient(this@AddStoryActivity)
                val response = apiService.addStory(descriptionRequestBody, photoPart, null, null)

                if (response.error == false) {
                    Toast.makeText(this@AddStoryActivity,
                        getString(R.string.story_added_successfully), Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                    loadingProgressBar.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this@AddStoryActivity, response.message ?: getString(R.string.failed_to_add_story), Toast.LENGTH_SHORT).show()
                    loadingProgressBar.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddStoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = View.INVISIBLE
            }
        }
    }
}

