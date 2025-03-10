package com.bitbyter.bookxpert_assignment.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitbyter.bookxpert_assignment.R
import com.bitbyter.bookxpert_assignment.api.RetrofitInstance
import com.bitbyter.bookxpert_assignment.database.AccountDatabase
import com.bitbyter.bookxpert_assignment.database.AccountEntity
import com.bitbyter.bookxpert_assignment.repository.AccountRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: AccountViewModel
    private lateinit var adapter: AccountAdapter
    private var currentImageUri: Uri? = null
    private var currentAccount: AccountEntity? = null

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentImageUri != null) {
                currentAccount?.let { account ->
                    viewModel.updateAccountImage(account.actid, currentImageUri.toString())
                }
            } else {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                currentAccount?.let { account ->
                    viewModel.updateAccountImage(account.actid, it.toString())
                }
            } ?: run {
                Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }


    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val speechText =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                speechText?.let { text ->
                    currentAccount?.let { account ->
                        viewModel.updateAlternateName(account, text)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkPermissions()) requestPermissions()

        val repository =
            AccountRepository(AccountDatabase.getInstance(this).accountDao(), RetrofitInstance.api)
        val factory = AccountViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[AccountViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AccountAdapter(
            emptyList(),
            onImageSelected = { account, uri ->
                currentAccount = account
                if (uri == null) {
                    currentImageUri = createImageUri()
                    imageCaptureLauncher.launch(currentImageUri)
                } else {
                    galleryLauncher.launch("image/*")
                }
            },
            onAlternateNameSet = { account, alternateName ->
                viewModel.updateAlternateName(account, alternateName)
            },
            onSpeechToText = { account ->
                currentAccount = account
                startSpeechRecognition()
            }
        )
        recyclerView.adapter = adapter

        viewModel.accounts.observe(this) { accounts ->
            adapter.updateList(accounts)
        }

        checkAndFetchAccounts()

        setupFab()
    }

    private fun checkAndFetchAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            val count = AccountDatabase.getInstance(this@MainActivity).accountDao().getCount()
            if (count == 0) {
                viewModel.fetchAccounts()
            }
        }
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            showFabOptions(view)
        }
    }

    private fun showFabOptions(view: View) {
        val options = arrayOf("Refresh Data", "Open PDF")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select an Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> refreshData()
                1 -> openPdf()
            }
        }
        builder.show()
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AccountDatabase.getInstance(this@MainActivity).accountDao()
            dao.clearAll()
            viewModel.fetchAccounts()
        }
    }

    private fun openPdf() {
        val pdfUrl =
            "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(pdfUrl), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        // Verify that there is a PDF viewer available
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No PDF viewer found!", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.RECORD_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri {
        val imageFile = File(getExternalCacheDir(), "temp_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)
    }


    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the alternate name")
        }
        speechRecognizerLauncher.launch(intent)
    }
}
