package com.tecsup.avisoscampus.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvisosRepositoryDataStore(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : AvisosRepository {

    private val clave = stringPreferencesKey("historial")
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos.asStateFlow()
    private var siguiente = 1

    init {
        scope.launch {
            val guardados = desdeJson(dataStore.data.first()[clave])
            _avisos.update { actuales -> actuales + guardados }
            siguiente = maxOf(siguiente, (guardados.maxOfOrNull { it.id } ?: 0) + 1)
        }
    }

    override fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val aviso = Aviso(siguiente++, titulo, texto, origen, hora)
        _avisos.update { listOf(aviso) + it }
        guardar()
        return aviso
    }

    override fun limpiar() {
        _avisos.value = emptyList()
        guardar()
    }

    private fun guardar() {
        scope.launch {
            dataStore.edit { preferencias ->
                preferencias[clave] = aJson(_avisos.value.take(MAXIMO))
            }
        }
    }

    private fun aJson(avisos: List<Aviso>): String {
        val arreglo = JSONArray()
        avisos.forEach { aviso ->
            arreglo.put(
                JSONObject()
                    .put("id", aviso.id)
                    .put("titulo", aviso.titulo)
                    .put("texto", aviso.texto)
                    .put("origen", aviso.origen.name)
                    .put("hora", aviso.hora)
            )
        }
        return arreglo.toString()
    }

    private fun desdeJson(texto: String?): List<Aviso> {
        if (texto.isNullOrBlank()) return emptyList()
        return runCatching {
            val arreglo = JSONArray(texto)
            (0 until arreglo.length()).mapNotNull { i ->
                val objeto = arreglo.getJSONObject(i)
                val origen = runCatching { Origen.valueOf(objeto.getString("origen")) }.getOrNull()
                    ?: return@mapNotNull null
                Aviso(
                    objeto.getInt("id"),
                    objeto.getString("titulo"),
                    objeto.getString("texto"),
                    origen,
                    objeto.getString("hora")
                )
            }
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val MAXIMO = 50
    }
}
