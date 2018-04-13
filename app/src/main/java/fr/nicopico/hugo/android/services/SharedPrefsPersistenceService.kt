package fr.nicopico.hugo.android.services

import android.content.Context
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.services.PersistenceService

private const val SELECTED_BABY_KEY = "selectedBaby"

class SharedPrefsPersistenceService(context: Context) : PersistenceService {

    private val prefs = context.getSharedPreferences("local", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val babyAdapter: JsonAdapter<Baby> = moshi.adapter(Baby::class.java)

    override suspend fun readBaby(): Baby? {
        return prefs.getString(SELECTED_BABY_KEY, null)
                ?.let { babyAdapter.fromJson(it) }
    }

    override suspend fun saveBaby(baby: Baby) {
        val babyJson = babyAdapter.toJson(baby)
        prefs.edit { putString(SELECTED_BABY_KEY, babyJson) }
    }
}