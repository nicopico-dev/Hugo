package fr.nicopico.hugo.android.services

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.services.PersistenceService

private const val SELECTED_BABY_KEY = "selectedBabyKey"

@SuppressLint("CommitPrefEdits")
class SharedPrefsPersistenceService(context: Context) : PersistenceService {

    private val prefs = context.getSharedPreferences("local", Context.MODE_PRIVATE)

    override suspend fun saveSelectedBaby(baby: Baby) {
        prefs.edit { putString(SELECTED_BABY_KEY, baby.key) }
    }

    override suspend fun isSelectedBaby(baby: Baby): Boolean {
        return prefs.getString(SELECTED_BABY_KEY, null) == baby.key
    }
}