package com.todokanai.displayalarm.tools.independent

/** 이 method들은 독립적으로 사용 가능함 */

import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.log10
import kotlin.math.pow

//----------------------
/** Todokanai
 *
 * Function to get the character sequence from after the last instance of File.separatorChar in a path
 * @author Neeyat Lotlikar
 * @param path String path representing the file
 * @return String filename which is the character sequence from after the last instance of File.separatorChar in a path
 * if the path contains the File.separatorChar. Else, the same path.*/
fun getFilenameForPath_td(path: String): String =
    if (!path.contains(File.separatorChar)) path
    else path.subSequence(
        path.lastIndexOf(File.separatorChar) + 1, // Discard the File.separatorChar
        path.length // parameter is used exclusively. Substring produced till n - 1 characters are reached.
    ).toString()

/** Todokanai */
fun readableFileSize_td(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}


/** Todokanai
 *
 * get the total size of [files]:Array<[File]> and its subdirectories
 * @param files Array of [File]
 * @return the total size
 * */
fun getTotalSize_td(files: Array<File>): Long {
    var totalSize: Long = 0
    for (file in files) {
        if (file.isDirectory) {
            totalSize += getTotalSize_td(file.listFiles() ?: emptyArray())
        } else {
            totalSize += file.length()
        }
    }
    return totalSize
}

/** Todokanai
 *
 * sort 적용된 fileList를 반환
 * */
fun sortedFileList_td(
    files:Array<File>,
    sortMode:String?
):List<File>{
    /** 하위 디렉토리 포함한 크기 */
    fun File.getTotalSize(): Long {
        var size: Long = 0
        if(this.isDirectory) {
            val listFiles = this.listFiles()
            if (listFiles != null) {
                for (file in listFiles) {
                    size += if (file.isDirectory) {
                        file.getTotalSize()
                    } else {
                        file.length()
                    }
                }
            }
        } else {
            return this.length()
        }
        return size
    }
    return when(sortMode){
        "BY_DEFAULT" ->{
            files.sortedWith (compareBy({it.isFile},{it.name}))
        }
        "BY_NAME_ASCENDING" ->{
            files.sortedBy{it.name}
        }
        "BY_NAME_DESCENDING" ->{
            files.sortedByDescending{it.name}
        }
        "BY_SIZE_ASCENDING" ->{
            files.sortedBy{ it.getTotalSize() }
        }
        "BY_SIZE_DESCENDING" ->{
            files.sortedByDescending { it.getTotalSize() }
        }
        "BY_TYPE_ASCENDING"->{
            files.sortedBy{it.extension}
        }
        "BY_TYPE_DESCENDING" ->{
            files.sortedByDescending { it.extension }
        }
        "BY_DATE_ASCENDING" ->{
            files.sortedBy{it.lastModified()}
        }
        "BY_DATE_DESCENDING" ->{
            files.sortedByDescending { it.lastModified() }
        } else -> {
            println("sortMode value error : $sortMode")
            files.toList()
        }
    }
}

/** Todokanai
 *
 * @return the number of [File] on [files] and its subdirectories. Does NOT Include directories
 * **/
fun getFileNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}
/** Todokanai
 * get the total number of files on [files] and its subdirectories
 * @param files Array of [File]
 * @return the total number
 * Directory와 File의 총 갯수*/
fun getFileAndFoldersNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total ++
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}

/** Todokanai
 * @return a fileTree from [currentPath]
 * */
fun dirTree_td(currentPath:File): List<File> {
    val result = mutableListOf<File>()
    var now = currentPath
    while (now.parentFile != null) {
        result.add(now)
        now = now.parentFile!!
    }
    return result.reversed()
}

fun zipFileEntrySize_td(file:java.util.zip.ZipFile):Long{
    var result = 0L

    val entries = file.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement() as ZipEntry
        result += entry.size
    }
    return result
}

/** Todokanai */
fun compressFilesRecursivelyToZip_td(files: Array<File>, zipFile: File) {
    val buffer = ByteArray(1024)
    val zipOutputStream = ZipOutputStream(zipFile.outputStream())

    fun addToZip(file: File, parentPath: String = "") {
        val entryName = if (parentPath.isNotEmpty()) "$parentPath/${file.name}" else file.name

        if (file.isFile) {
            val zipEntry = ZipEntry(entryName)
            zipOutputStream.putNextEntry(zipEntry)

            val inputStream = FileInputStream(file)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                zipOutputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            zipOutputStream.closeEntry()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach { childFile ->
                addToZip(childFile, entryName)
            }
        }
    }
    for (file in files) {
        addToZip(file)
    }
    zipOutputStream.close()
    println("파일 압축이 완료되었습니다.")
}

/** Todokanai
 *
 * 경로가 접근 가능할 경우 true 반환
 * **/
fun isAccessible_td(file: File): Boolean {
    return file.listFiles() != null
}

/**
 * Function to get the MimeType from a filename by comparing it's file extension
 * @author Neeyat Lotlikar
 * @param filename String name of the file. Can also be a path.
 * @return String MimeType */
fun getMimeType_td(filename: String): String = if (filename.lastIndexOf('.') == -1)
    "resource/folder"
else
    when (filename.subSequence(
        filename.lastIndexOf('.'),
        filename.length
    ).toString().lowercase(Locale.ROOT)) {
        ".doc", ".docx" -> "application/msword"
        ".pdf" -> "application/pdf"
        ".ppt", ".pptx" -> "application/vnd.ms-powerpoint"
        ".xls", ".xlsx" -> "application/vnd.ms-excel"
        ".zip", ".rar" -> "application/x-wav"
        ".7z" -> "application/x-7z-compressed"
        ".rtf" -> "application/rtf"
        ".wav", ".mp3", ".m4a", ".ogg", ".oga", ".weba" -> "audio/*"
        ".ogx" -> "application/ogg"
        ".gif" -> "image/gif"
        ".jpg", ".jpeg", ".png", ".bmp" -> "image/*"
        ".csv" -> "text/csv"
        ".m3u8" -> "application/vnd.apple.mpegurl"
        ".txt", ".mht", ".mhtml", ".html" -> "text/plain"
        ".3gp", ".mpg", ".mpeg", ".mpe", ".mp4", ".avi", ".ogv", ".webm" -> "video/*"
        else -> "*/*"
    }
