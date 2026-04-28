package com.example.budjet

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.Expense
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: com.example.budjet.data.BudJetDao

    private lateinit var btnTakePhoto: Button
    private lateinit var btnPickGallery: Button
    private lateinit var ivPhotoPreview: ImageView
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private val CAMERA_PERMISSION_CODE = 100
    private val GALLERY_PERMISSION_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = AppDatabase.getInstance(this)
        dao = db.budJetDao()


        val btnSave = findViewById<Button>(R.id.btnSaveExpense)
        val etCategory = findViewById<AppCompatAutoCompleteTextView>(R.id.etCategory)
        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etStartTime = findViewById<TextInputEditText>(R.id.etStartTime)
        val etEndTime = findViewById<TextInputEditText>(R.id.etEndTime)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnPickGallery = findViewById(R.id.btnPickGallery)
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview)


        val categories = listOf("Groceries", "Entertainment", "Clothing", "Maintenance", "Utilities", "Travel")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)
        etCategory.threshold = 1


        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        etDate.setText(currentDate)
        etStartTime.setText(currentTime)
        etEndTime.setText(currentTime)


        btnTakePhoto.setOnClickListener { checkCameraPermissionAndLaunch() }
        btnPickGallery.setOnClickListener { checkGalleryPermissionAndLaunch() }

        btnSave.setOnClickListener {
            val category = etCategory.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()

            if (category.isEmpty() || amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("currentUserId", 1)

            val expense = Expense(
                expenseId = 0,
                userId = userId,
                category = category,
                amount = amount,
                description = description,
                date = date,
                startTime = startTime,
                endTime = endTime,
                photoPath = currentPhotoPath
            )

            lifecycleScope.launch {
                dao.insertExpense(expense)
                Toast.makeText(this@AddExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            launchCamera()
        }
    }

    private fun checkGalleryPermissionAndLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), GALLERY_PERMISSION_CODE)
            } else {
                launchGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_PERMISSION_CODE)
            } else {
                launchGallery()
            }
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (e: IOException) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
                null
            }
            photoFile?.let {
                photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "EXP_$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery()
                } else {
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    photoUri?.let {
                        showPhotoPreview(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        photoUri = uri

                        val savedPath = saveGalleryImageToPrivateFile(uri)
                        if (savedPath != null) {
                            currentPhotoPath = savedPath
                            showPhotoPreview(uri)
                        } else {
                            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun saveGalleryImageToPrivateFile(sourceUri: Uri): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val destFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EXP_$timeStamp.jpg")
            contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                destFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showPhotoPreview(uri: Uri) {
        ivPhotoPreview.setImageURI(uri)
        ivPhotoPreview.visibility = ImageView.VISIBLE
        btnTakePhoto.text = "Retake Photo"
        btnPickGallery.text = "Change Photo"
    }
}