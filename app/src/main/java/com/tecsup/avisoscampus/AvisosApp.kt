package com.tecsup.avisoscampus

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.data.AvisosRepositoryDataStore
import com.tecsup.avisoscampus.notificacion.Notificador
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "avisos")

class AvisosApp : Application() {

    val repositorio: AvisosRepository by lazy {
        AvisosRepositoryDataStore(dataStore, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }

    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
