package com.example.qrcodegenerator

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.qrcodegenerator.ui.theme.QRcodeGeneratorTheme

class MainActivity : ComponentActivity() {
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
}

@Composable
fun EditTexts(context: Context) {
    var name by remember { mutableStateOf("Yasu") }
    var email by remember { mutableStateOf("yasunari.k@mail.com") }
    var registerDate by remember { mutableStateOf("2023-04-07") }

    Column {
        TextField(value = name, onValueChange = { name = it }, label = { Text(text = "Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text(text = "Email") })
        TextField(value = registerDate, onValueChange = { registerDate = it }, label = { Text(text = "RegisterDate") })
        Button(
            onClick = {
                Toast.makeText(context, "QR Code is supposed to be generated :)", Toast.LENGTH_SHORT).show()
                generateQrCode(name, email, registerDate)
            }
        ) {
            Text(text = "QR Code")
        }
    }
}

fun generateQrCode(
    name: String,
    email: String,
    registerDate: String
) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QRcodeGeneratorTheme {
        //EditTexts()
    }
}