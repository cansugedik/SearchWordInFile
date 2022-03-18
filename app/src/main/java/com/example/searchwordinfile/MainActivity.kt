package com.example.searchwordinfile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.searchwordinfile.FilePath.arananSozcukListesi
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var progressBar: ProgressBar? = null
    var select_file: Button? = null
    var listview: ListView? = null
    var txt_search: EditText? = null
    var txt_mask: EditText? = null
    var txt_path: EditText? = null
    var txt_name: EditText? = null
    var txt_size: TextView? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        select_file = findViewById(R.id.select_from_file)
        listview = findViewById(R.id.listview)
        txt_path = findViewById(R.id.txt_path)
        txt_mask = findViewById(R.id.txt_mask)
        txt_search = findViewById(R.id.txt_search)
        txt_name = findViewById(R.id.txt_name)
        txt_size = findViewById(R.id.txt_size)
        progressBar = findViewById(R.id.progressbar)

        findViewById<Button>(R.id.select_from_file).setOnClickListener(this);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        if (v.id == R.id.select_from_file) {
            arananSozcukListesi = ArrayList<String>()
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    filePicker()
                } else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                filePicker()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filePicker() {
        FilePath.getFilePath(
            this@MainActivity,
            txt_path!!.text.toString(),
            txt_name!!.text.toString(),
            txt_mask!!.text.toString(),
            txt_search!!.text.toString()
        )
        val veriAdaptoru: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            arananSozcukListesi
        )
        listview!!.adapter = veriAdaptoru
        txt_size!!.text = "Toplam bul. sözcük : " + (arananSozcukListesi?.size ?: 0)
    }

    private fun checkPermission(permission: String): Boolean {
        val result: Int = ContextCompat.checkSelfPermission(this@MainActivity, permission)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }

    private fun requestPermission(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permission)) {
            Toast.makeText(this@MainActivity, "Please Allow Permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(permission),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Permission Successfull", Toast.LENGTH_SHORT)
                    .show()
                filePicker()
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}