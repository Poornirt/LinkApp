package com.linkapp.helper

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.linkapp.helper.ViewExt.showToast
import com.linkapp.jdo.Link
import java.io.File
import java.io.FileNotFoundException


const val FILE_NAME = "LinkData.csv"

object FileUtils {

    fun backupData(context: Context, mLinkList: List<Link>, showShareOption: Boolean): File? {
        val csvFile = generateFile()
        try {
            csvFile?.let {
                csvWriter().open(csvFile, append = false) {
                    writeRow(listOf("[id]", "[name]", "[url]", "[image_url]"))
                    mLinkList.forEach {
                        writeRow(listOf(it.id, it.name, it.link_url, it.image_url))
                    }
                }
                if (showShareOption) {
                    shareAttachmentViaEmail(context, csvFile)
                } else {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(csvFile.toString()),
                        null
                    ) { p0, p1 ->
                        context.showToast("Downloaded")
                        Log.d("FileUtils", "Scanning completed")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return csvFile
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String {
        var filePath = ""
        try {
            val wholeID: String = DocumentsContract.getDocumentId(uri)

            val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val column = arrayOf(MediaStore.Files.FileColumns.DATA)

            val sel = MediaStore.Files.FileColumns._ID + "=?"

            val cursor = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                column, sel, arrayOf(id), null
            )

            val columnIndex = cursor!!.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            filePath = ""
        }
        return filePath
    }

    fun restoreFromFile(context: Context,filePath:String): List<Map<String, String>> {
        try {
            val file = if (filePath.isNotEmpty()) {
                File(filePath)
            } else {
                val downloadDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                File("$downloadDirectory/LinkApp/$FILE_NAME")
            }
            return csvReader().readAllWithHeader(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            context.showToast("LinkData.csv file not found in download folder")
            return emptyList()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    private fun shareAttachmentViaEmail(context: Context, path: File) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.apply {
            val contentUri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", path)
            putExtra(Intent.EXTRA_STREAM, contentUri)
            setDataAndType(contentUri, "text/csv")
            data = contentUri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
    }


    private fun generateFile(): File? {
        val downloadDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return try {
            val directory = File("$downloadDirectory/LinkApp")
            directory.mkdirs()
            if (directory.exists()) directory.delete()
            val csvFile = File(directory, FILE_NAME)
            csvFile.createNewFile()
            if (csvFile.exists()) {
                csvFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
//    /data/user/0/com.linkapp/files/LinkData.db
}