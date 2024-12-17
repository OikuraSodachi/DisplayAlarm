package com.todokanai.displayalarm.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todokanai.displayalarm.abstracts.MyDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(appContext: Context): MyDataStore(appContext){
    companion object{
        val DATASTORE_STRING = stringPreferencesKey("datastore_sort_by")
        val DATASTORE_FILE_PATH = stringPreferencesKey("datastore_file_path")
    }
    suspend fun saveString(value:String) = DATASTORE_STRING.save(value)

    suspend fun getString() = DATASTORE_STRING.value()

    val stringFlow = DATASTORE_STRING.flow()

    suspend fun saveFilePath(value: String) = DATASTORE_FILE_PATH.save(value)

    suspend fun getFilePath() = DATASTORE_FILE_PATH.value()

    val filePath = DATASTORE_FILE_PATH.flow()
}