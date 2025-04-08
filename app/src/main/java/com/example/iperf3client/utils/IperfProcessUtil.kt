package com.example.iperf3client.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import com.example.iperf3client.R
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream


class IperfProcessUtil {



    fun changePermissions(context: Context, filePath: String) {
        val permission = "775"
        shellExec("")

        //context.resources.openRawResource(R.raw.iperf3)


    }





    companion object {

        fun iperfBinInit() {


/*
            shellExec("whoami")
            shellExec("/data/local/tmp/iperf")
            val path = context.filesDir
            shellExec("chmod 777 $path/iperf")
            shellExec("./data/app/iperf -s")*/


        }

        fun shellExec(cmd: String?): String? {
            var o: String? = null
            try {
                val p = Runtime.getRuntime().exec(cmd)
                val b = BufferedReader(InputStreamReader(p.inputStream))
                var line: String?
                while ((b.readLine().also { line = it }) != null) o += "\n$line"

            } catch (e: Exception) {
                o = "error"
            }
            return o
        }
        /*fun copyIperfToTmp(context: Context, applicationInfo: ApplicationInfo) {
            val pathS = applicationInfo.nativeLibraryDir
            var input: InputStream? = context.resources.openRawResource(R.raw.libiperf3)


            //val result = File(applicationInfo.nativeLibraryDir, "iperf").createNewFile()
            //var output: FileOutputStream? = FileOutputStream(File("/data/local/tmp", "iperf"))
            var output: FileOutputStream? = FileOutputStream(File(applicationInfo.nativeLibraryDir, "iperf"))
            try {
                if (input != null) {
                    if (output != null) {
                        copyFile(input, output)
                        input.close()
                        input = null
                        output.flush()
                        output.close()
                        output = null
                    }
                }
            } catch (e: IOException) {

            }
            val path = context.filesDir
            shellExec("chmod 777 $path/iperf")
            shellExec("./data/app/iperf -s")
        }*/

        @Throws(IOException::class)
        private fun copyFile(`in`: InputStream, out: OutputStream) {
            val buffer = ByteArray(1024)
            var read: Int
            while ((`in`.read(buffer).also { read = it }) != -1) {
                out.write(buffer, 0, read)
            }
        }
    }

}
/*
private fun copyAssets() {
    val assetManager: AssetManager = getAssets()
    var files: Array<String>? = null
    try {
        files = assetManager.list("")
    } catch (e: IOException) {
        Log.e("tag", "Failed to get asset file list.", e)
    }
    for (filename in files!!) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(filename)

            val outDir = Environment.getExternalStorageDirectory().absolutePath + "/Download/"

            val outFile = File(outDir, filename)

            out = FileOutputStream(outFile)
            copyFile(`in`, out)
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
        } catch (e: IOException) {
            Log.e("tag", "Failed to copy asset file: $filename", e)
        }
    }
}*/


