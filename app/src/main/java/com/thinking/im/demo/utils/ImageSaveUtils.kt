package com.thinking.im.demo.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files

object ImageSaveUtils {

    private const val TAG = "ImageSaveUtils"

    /**
     * 将图片文件保存到系统相册
     */
    fun saveImgFileToAlbum(context: Context, imageFilePath: String): Boolean {
        Log.d(TAG, "saveImgToAlbum() imageFile = [$imageFilePath]")
        try {
            val bitmap = BitmapFactory.decodeFile(imageFilePath)
            return saveBitmapToAlbum(context, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 将bitmap保存到系统相册
     */
    private fun saveBitmapToAlbum(context: Context, bitmap: Bitmap): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            saveBitmapToAlbumBeforeQ(context, bitmap)
        } else {
            saveBitmapToAlbumAfterQ(context, bitmap)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun saveBitmapToAlbumAfterQ(context: Context, bitmap: Bitmap): Boolean {
        val contentUri =
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            }
        val contentValues = getImageContentValues(context)
        val uri = context.contentResolver.insert(contentUri, contentValues) ?: return false
        var os: OutputStream? = null
        try {
            os = context.contentResolver.openOutputStream(uri) ?: return false
            if (bitmap.hasAlpha()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            }
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveBitmapToAlbumBeforeQ(context: Context, bitmap: Bitmap): Boolean {
        val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val destFile = File(
            picDir, context.packageName + File.separator + System.currentTimeMillis() + ".jpg"
        )
        var os: OutputStream? = null
        var result = false
        try {
            if (!destFile.exists()) {
                destFile.getParentFile()?.mkdirs()
                destFile.createNewFile()
            }
            os = BufferedOutputStream(FileOutputStream(destFile))
            result = if (bitmap.hasAlpha()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        MediaScannerConnection.scanFile(
            context, arrayOf(destFile.absolutePath), arrayOf("image/*")
        ) { path, uri ->
            Log.d(TAG, "saveVideoToAlbum: $path $uri");
            // Scan Completed
        }
        return result
    }

    /**
     * 获取图片的ContentValue
     *
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getImageContentValues(context: Context): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg"
        )
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
        contentValues.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator + context.packageName
        )
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis())
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        return contentValues
    }

    /**
     * 将视频保存到系统相册
     */
    fun saveVideoToAlbum(context: Context, videoFile: String): Boolean {
        Log.d(TAG, "saveVideoToAlbum() videoFile = [$videoFile]")
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            saveVideoToAlbumBeforeQ(context, videoFile)
        } else {
            saveVideoToAlbumAfterQ(context, videoFile)
        }
    }

    private fun saveVideoToAlbumAfterQ(context: Context, videoFile: String): Boolean {
        try {
            val contentResolver = context.contentResolver
            val tempFile = File(videoFile)
            val contentValues = getVideoContentValues(context, tempFile, System.currentTimeMillis())
            val uri =
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                copyFileAfterQ(context, contentResolver, tempFile, uri)
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun saveVideoToAlbumBeforeQ(context: Context, videoFile: String): Boolean {
        val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val tempFile = File(videoFile)
        val destFile = File(
            picDir, context.packageName + File.separator + tempFile.getName()
        )
        var ins: FileInputStream? = null
        var ous: BufferedOutputStream? = null
        try {
            ins = FileInputStream(tempFile)
            ous = BufferedOutputStream(FileOutputStream(destFile))
            val buf = ByteArray(1024)
            var nRead = 0L
            while (true) {
                val n = ins.read(buf)
                if (n > 0) {
                    ous.write(buf, 0, n)
                    nRead += n
                } else {
                    break
                }
            }
            MediaScannerConnection.scanFile(
                context, arrayOf(destFile.absolutePath), arrayOf("video/*")
            ) { path, uri ->
                Log.d(TAG, "saveVideoToAlbum: $path $uri");
                // Scan Completed
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                ins?.close()
                ous?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFileAfterQ(
        context: Context, localContentResolver: ContentResolver, tempFile: File, localUri: Uri
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.Q) {
            //拷贝文件到相册的uri,android10及以上得这么干，否则不会显示。可以参考ScreenMediaRecorder的save方法
            val os = localContentResolver.openOutputStream(localUri)
            os?.let {
                Files.copy(tempFile.toPath(), it)
                it.close()
            }
            tempFile.delete()
        }
    }


    /**
     * 获取视频的contentValue
     */
    private fun getVideoContentValues(
        context: Context,
        paramFile: File,
        timestamp: Long
    ): ContentValues {
        val localContentValues = ContentValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            localContentValues.put(
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DCIM + File.separator + context.getPackageName()
            )
        }
        localContentValues.put(MediaStore.Video.Media.TITLE, paramFile.getName())
        localContentValues.put(MediaStore.Video.Media.DISPLAY_NAME, paramFile.getName())
        localContentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        localContentValues.put(MediaStore.Video.Media.DATE_TAKEN, timestamp)
        localContentValues.put(MediaStore.Video.Media.DATE_MODIFIED, timestamp)
        localContentValues.put(MediaStore.Video.Media.DATE_ADDED, timestamp)
        localContentValues.put(MediaStore.Video.Media.SIZE, paramFile.length())
        return localContentValues
    }
}