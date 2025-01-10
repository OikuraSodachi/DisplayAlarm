package com.todokanai.displayalarm.tools.independent

/**
 * 독립적으로 사용 가능하고, Android 의존성 있는 method 모음
 */
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

/** Todokanai
 * Application Shutdown method
 * @param activity [Activity] to finish
 * @param serviceIntent [Intent] that starts service
 */
fun exit_td(activity: Activity, serviceIntent: Intent? = null){
    ActivityCompat.finishAffinity(activity)
    serviceIntent?.let{ activity.stopService(it) }     // 서비스 종료
    System.runFinalization()
    exitProcess(0)
}

suspend fun getThumbnail_td(file: File,width:Int = 100,height:Int = 100): Bitmap = withContext(Dispatchers.IO){
    ThumbnailUtils.extractThumbnail(
        BitmapFactory.decodeFile(file.absolutePath), width, height)
}

fun durationText_td(duration:Int?):String{
    if(duration == null){
        return "null"
    } else if(duration < 3600000){
        return SimpleDateFormat("mm:ss").format(duration)
    }else {
        return SimpleDateFormat("hh:mm:ss").format(duration)
    }
}

/**
 *  looper.
 *
 */
fun callHandler_td(
    handler:Handler,
    callback:()->Unit
){
    handler.post({callback()})
}

/*

/** Logcat Tag 매크로 용도 **/
fun setupTimber_td() {
    Timber.plant(
        object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                val className = super.createStackElementTag(element)
                return "TAG $className ${element.methodName}"
            }
        }
    )
}
 */

fun isPermissionGranted_td(activity: Activity, permission: String):Boolean{
    val result = ContextCompat.checkSelfPermission(activity,permission)
    return result == PackageManager.PERMISSION_GRANTED
}


/** requestCode를 111로 냅둬도 무방한게 정말 맞는지 확인 필요
 *
 * system에 permission n개를 한번에 요청함
 **/
fun requestPermission_td(
    activity: Activity,
    permissions: Array<String>,
    permissionNotice:()->Unit,
    requestCode:Int = 111
){
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permissions.first()
        )
    ) {
        permissionNotice()
    } else {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }
}

/** Todokanai
 *
 * == Toast.makeText(appContext,text,Toast.LENGTH_SHORT).show() **/
fun ToastShort_td(appContext: Context, text:String){
    Toast.makeText(appContext,text,Toast.LENGTH_SHORT).show()
}

/** Todokanai
 *
 * @param context a [Context] instance
 * @return list of physical storages available
 * **/
fun getPhysicalStorages_td(context: Context):List<File>{
    val defaultStorage = Environment.getExternalStorageDirectory()
    val volumes = context.getSystemService(StorageManager::class.java)?.storageVolumes
    val storageList = mutableListOf<File>(defaultStorage)
    volumes?.forEach { volume ->
        if (!volume.isPrimary && volume.isRemovable) {
            val sdCard = volume.directory
            if (sdCard != null) {
                storageList.add(sdCard)
            }
        }
    }
    return storageList
}

/** Todokanai
 *
 * 모든 파일 접근 권한 요청 **/
fun requestStorageManageAccess_td(activity: Activity){
    val intent = Intent()
    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
    val uri: Uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
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
fun openFile_td(
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
    println("mimeType: $mimeType")
    try {
        ActivityCompat.startActivity(context, intent, null)
    } catch (t:Throwable){
        println(t)
        onFailure()
    }
}

/** Todokanai
 *
 *  itemList: List of Pair<( ItemTitle ),( Callback ) >
 *
 * **/
fun popupMenu_td(
    context: Context,
    anchor:View,
    itemList:List<Pair<String,()->Unit>>,
    gravity:Int = 0  // == Gravity.NO_GRAVITY
){
    val popupMenu = PopupMenu(context,anchor,gravity)
    popupMenu.run {
        itemList.forEach{
            menu.add(it.first)
        }
        setOnMenuItemClickListener { item ->
            itemList.forEach{
                if(item.title == it.first){
                    it.second()
                }
            }
            false
        }
        show()
    }
}

/**
 * Get a file path from a Uri. This will get the the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.
 *
 * @param context The context.
 * @param uri The Uri to query.
 * @author paulburke
 */
fun getPathFromUri_td(context: Context, uri: Uri): String? {

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )

        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }

            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLong()
            )

            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )

            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }

    return null
}