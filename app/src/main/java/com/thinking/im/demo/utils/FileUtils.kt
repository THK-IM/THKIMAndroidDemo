package com.thinking.im.demo.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


object FileUtils {

    /**
     * 解压assets的zip压缩文件到指定目录(包含子目录)
     * @param zipFileName 压缩文件名
     * @param outputDirectory 输出目录
     * @throws IOException
     */
    fun unZip(zipFileName: String, outputDirectory: String): Boolean {
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(zipFileName)
            val e: Enumeration<*> = zipFile.entries()
            var zipEntry: ZipEntry
            val dest = File(outputDirectory)
            dest.mkdirs()
            while (e.hasMoreElements()) {
                zipEntry = e.nextElement() as ZipEntry
                val entryName = zipEntry.name
                var inputStream: InputStream? = null
                var out: FileOutputStream? = null
                if (zipEntry.isDirectory) {
                    var name = zipEntry.name
                    name = name.substring(0, name.length - 1)
                    val f = File(outputDirectory + File.separator + name)
                    f.mkdirs()
                } else {
                    var index = entryName.lastIndexOf("\\")
                    if (index != -1) {
                        val df = File(
                            (outputDirectory + File.separator + entryName.substring(0, index))
                        )
                        df.mkdirs()
                    }
                    index = entryName.lastIndexOf("/")
                    if (index != -1) {
                        val df = File(
                            (outputDirectory + File.separator + entryName.substring(0, index))
                        )
                        df.mkdirs()
                    }
                    val f = File(
                        (outputDirectory + File.separator + zipEntry.name)
                    )
                    inputStream = zipFile.getInputStream(zipEntry)
                    out = FileOutputStream(f)

                    var c: Int
                    val by = ByteArray(1024)

                    while ((inputStream.read(by).also { c = it }) != -1) {
                        out.write(by, 0, c)
                    }
                    out.flush()
                }
            }
            return true
        } catch (ex: IOException) {
            ex.printStackTrace()
            return false
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    /**
     * 删除整个文件夹 或者 文件
     */
    private fun deleteDir(file: File) {
        if (!file.exists()) {
            return
        }
        if (file.isDirectory) {
            val childFiles = file.listFiles()
            if (childFiles == null || childFiles.isEmpty()) {
                file.delete()
            }
            for (childFile in childFiles!!) {
                deleteDir(childFile)
            }
        }
        file.delete()
    }

}