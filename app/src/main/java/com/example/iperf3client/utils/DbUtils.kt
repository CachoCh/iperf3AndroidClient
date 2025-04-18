package com.example.iperf3client.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
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
    }
}