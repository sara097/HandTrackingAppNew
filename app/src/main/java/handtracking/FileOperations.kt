package handtracking

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


internal class FileOperations(
        val context: Context,
        val name: String,
        val text: String
) {

    private fun generateFileName(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd;HH:mm:ss")
        val date = Date()
        return "$name-${dateFormat.format(date)}"
    }


    fun saveData(append: Boolean = true) {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "GESTURES")
            !file.mkdirs()
            val myFile = File(file, "${generateFileName()}.txt")

            val fOut = FileOutputStream(myFile, append)
            val out = OutputStreamWriter(fOut)
            //zapisanie do pliku
            out.write(text.replace("No hand landmarks", ""))
            out.flush()
            out.close()

            //wyswietlenie komunikatu, że zapisano dane
            Toast.makeText(context, "Data Saved", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            //obsluga wyjatku
            //w razie niepowodzenia zapisu do pliku zostaje wyswietlony komunikat a w konsoli zrzut stosu
            Toast.makeText(context, "Data Could not be added", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}