package com.todokanai.displayalarm.tools.independent

import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import java.io.File

class FileModule(defaultPath:File) {

    /** 현재 보고있는 Directory
     *
     *  Primary Key(?)
     * **/
    private val _currentPath = MutableStateFlow(defaultPath)
    val currentPath : Flow<File>
        get() = _currentPath

    /** array of files to show **/
    val listFiles = currentPath.map {
        it.listFiles() ?: emptyArray()
    }.shareIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.WhileSubscribed(5)
    )

    /** directory tree **/
    val dirTree = currentPath.map { it.dirTree() }

    fun updateCurrentPath(directory:File){
        if(directory.listFiles()!=null) {        // 접근 가능 여부 체크
            _currentPath.value = directory
        }
    }

    /** whether currentPath is Accessible **/
    val notAccessible = currentPath.map { it.listFiles() == null }

    /** Todokanai
     *
     *  == File.dirTree_td()
     * */
    private fun File.dirTree(): List<File> {
        val result = mutableListOf<File>()
        var now = this
        while (now.parentFile != null) {
            result.add(now)
            now = now.parentFile!!
        }
        return result.reversed()
    }

    /** Todokanai
     *
     *  open the file with a compatible application
     *
     *  requires ContentProvider
     *
     *  onFailure: no application available to open the file, etc...
     *
     *  mimeType: Mime type of the given file
     * **/
    private fun openFile_td(
        context: Context,
        file: File,
        mimeType:String,
        onFailure:()->Unit = {}
    ){
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(
            FileProvider.getUriForFile(context,
                "${context.packageName}.provider",
                file
            ), mimeType
        )
        // println("mimeType: $mimeType")
        try {
            ActivityCompat.startActivity(context, intent, null)
        } catch (t:Throwable){
            println(t)
            onFailure()
        }
    }
}