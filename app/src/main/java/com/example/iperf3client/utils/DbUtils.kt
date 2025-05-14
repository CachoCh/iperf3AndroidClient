package com.example.iperf3client.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.example.iperf3client.data.TestDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class DbUtils {
    companion object {
        fun backupDatabase(context: Context, databaseName: String, backupLocation: File) {
            val dbPath = context.getDatabasePath(databaseName).absolutePath
            val dbFile = File(dbPath)
            val backupFile = File(backupLocation, databaseName)

            FileInputStream(dbFile).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }
            // Verify the backup
            if (backupFile.exists() && backupFile.length() == dbFile.length()) {
                Log.d("Backup", "Database backup successful: ${backupFile.absolutePath}")
                Toast.makeText(
                    context, "Database backup successful: ${backupFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.e("Backup", "Database backup failed.")
                Toast.makeText(
                    context, "Database backup failed.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun shareCsvFile(context: Context) {
            val shareFile = context.getDatabasePath(TestDatabase.DATABASE_NAME)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setType("text/csv")

            val uri =FileProvider.getUriForFile(context, context.packageName + ".provider", shareFile)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "share"
                ))
        }

        fun shareRoomDatabase(
            context: Context,
            dbName: String,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
        ) {
            val dbFile = context.getDatabasePath(dbName)
            val sharedDb = File(context.filesDir, TestDatabase.DATABASE_NAME)

            dbFile.copyTo(sharedDb, overwrite = true)
            Log.d("DB_SHARE", "Database path: ${dbFile.absolutePath}")

            if (dbFile.exists()) {
                val fileUri: Uri = FileProvider.getUriForFile(
                    context,
                    "com.example.iperf3client.provider", // must match manifest authority
                    sharedDb
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/octet-stream"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                launcher.launch(Intent.createChooser(shareIntent, "Share Database"))
            } else {
                Toast.makeText(context, "Database file not found", Toast.LENGTH_SHORT).show()
            }
        }

    }
}