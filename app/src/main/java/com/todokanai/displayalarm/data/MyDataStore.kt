package com.todokanai.displayalarm.data

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

class MyDataStore(val context: Context) {
    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mydatastore")

        val DATASTORE_SOUND_FILE = stringPreferencesKey("datastore_sound_file")
    }

    suspend fun saveFilePath(filePath:String){
        context.dataStore.edit {
            it[DATASTORE_SOUND_FILE] = filePath
        }
    }

    suspend fun getFilePath():String?{
        return context.dataStore.data.first()[DATASTORE_SOUND_FILE]
    }

    val filePath : Flow<String?> = context.dataStore.data.map{
        it[DATASTORE_SOUND_FILE]
    }
}