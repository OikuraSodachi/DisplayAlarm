package com.todokanai.displayalarm.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todokanai.displayalarm.abstracts.MyDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(appContext: Context): MyDataStore(appContext){
    companion object{
        private val DATASTORE_STRING = stringPreferencesKey("datastore_sort_by")
        private val DATASTORE_FILE_PATH = stringPreferencesKey("datastore_file_path")
        private val DATASTORE_FILE_URI_STRING = stringPreferencesKey("datastore_file_uri")
        private val DATASTORE_START_HOUR = intPreferencesKey("datastore_start_hour")
        private val DATASTORE_START_MIN = intPreferencesKey("datastore_start_min")
        private val DATASTORE_END_HOUR = intPreferencesKey("datastore_end_hour")
        private val DATASTORE_END_MIN = intPreferencesKey("datastore_end_min")
    }
    suspend fun saveString(value:String) = DATASTORE_STRING.save(value)
    suspend fun getString() = DATASTORE_STRING.value()
    val stringFlow = DATASTORE_STRING.flow()

    suspend fun saveFilePath(value: String) = DATASTORE_FILE_PATH.save(value)
    suspend fun getFilePath() = DATASTORE_FILE_PATH.value()
    val filePath = DATASTORE_FILE_PATH.flow()

    suspend fun saveFileUriString(value:String) = DATASTORE_FILE_URI_STRING.save(value)
    suspend fun getFileUriString() = DATASTORE_FILE_URI_STRING.value()?.toUri()
    suspend fun getFileUri() = DATASTORE_FILE_URI_STRING.value()?.toUri()
    val fileUriStringFlow = DATASTORE_FILE_URI_STRING.flow()

    suspend fun saveStartHour(value:Int) = DATASTORE_START_HOUR.save(value)
    suspend fun getStartHour() = DATASTORE_START_HOUR.value()
    val startHourFlow = DATASTORE_START_HOUR.flow()

    suspend fun saveStartMin(value:Int) = DATASTORE_START_MIN.save(value)
    suspend fun getStartMin() = DATASTORE_START_MIN.value()
    val startMinFlow = DATASTORE_START_MIN.flow()

    suspend fun saveEndHour(value:Int) = DATASTORE_END_HOUR.save(value)
    suspend fun getEndHour() = DATASTORE_END_HOUR.value()
    val endHourFlow = DATASTORE_END_HOUR.flow()

    suspend fun saveEndMin(value:Int) = DATASTORE_END_MIN.save(value)
    suspend fun getEndMin() = DATASTORE_END_MIN.value()
    val endMinFlow = DATASTORE_END_MIN.flow()
}