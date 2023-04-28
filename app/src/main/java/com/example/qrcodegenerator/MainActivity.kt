package com.example.qrcodegenerator

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import com.example.qrcodegenerator.ui.theme.QRcodeGeneratorTheme
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    companion object {
        const val FILE_AUTHORITY = "com.example.fileprovider"
        const val imageFileName = "qrCodeForLibrary.png"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRcodeGeneratorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    EditTexts(this)
                }
            }
        }
    }

    @Composable
    fun EditTexts(context: Context) {
        var name by remember { mutableStateOf("日本語センター　図書太郎") }
        var email by remember { mutableStateOf("test@hotmail.com") }
        var registerDate by remember { mutableStateOf("2023-04-07") }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextField(value = name, onValueChange = { name = it }, label = { Text(text = "氏名") })
            TextField(value = email, onValueChange = { email = it }, label = { Text(text = "メールアドレス") })
            TextField(value = registerDate, onValueChange = { registerDate = it }, label = { Text(text = "会員登録日") })
            Button(
                onClick = {
                    Toast.makeText(context, "QRコード発行中・・・", Toast.LENGTH_SHORT).show()
                    val qrCodeFile = generateQrCode(name, email, registerDate)
                    sendEmail(qrCodeFile, name, email)
                }
            ) {
                Text(text = "QRコード発行")
            }
        }
    }

    private fun generateQrCode(
        name: String,
        email: String,
        registerDate: String
    ): File {
        deleteImageFIle()

        val content = "{ " +
                "\"name\":\"$name\", " +
                "\"date\":\"$registerDate\", " +
                "\"email\":\"$email\" " +
                "}"

        val file = createImageFIle()
        Log.d("generateQrCode", "filePath=${file.path}")

        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400)

        try {
            val fileOutputStream = FileOutputStream(File(file.path), true)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    private fun sendEmail(
        qrFile: File,
        name: String,
        emailAddress: String
    ) {
        try {
            val subject = "【モントリオール日本語センター】図書室QRコード発行"
            val message = "$name 様\n\n" +
                    "図書の貸し借りのためのQRコードが発行されました。" +
                    "これから一年間、図書室を利用の際は図書係にQRコードをスキャンしてもらって、本の貸し借り手続きを行ってください。\n\n" +
                    "よろしくお願いいたします。\n\n" +
                    "モントリオール日本語センター 図書係"

            val uriImage = FileProvider.getUriForFile(applicationContext, FILE_AUTHORITY, qrFile)

            this.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_STREAM, uriImage)
                        putExtra(Intent.EXTRA_TEXT, message)
                        clipData = ClipData.newRawUri(null, uriImage)
                        type = "image/png"
                    },
                    "share title"
                )
            )
        } catch (t: Throwable) {
            Log.e("SendEmail", "error = $t")
            Toast.makeText(this, "Request failed, retry! $t", Toast.LENGTH_LONG).show()
        }
    }

    private fun createImageFIle(): File {
        val file = File(cacheDir.absolutePath, imageFileName)

        if(file.createNewFile()){
            Log.d("createImageFile", "File created")
        } else {
            Log.d("createImageFile", "File already exists")
        }

        return file
    }

    private fun deleteImageFIle() {
        val file = File(cacheDir.absolutePath, imageFileName)

        if(file.delete()) {
            Log.d("deleteImageFile", "File deleted")
        }else{
            Log.d("deleteImageFile", "File not deleted")
        }
    }
}