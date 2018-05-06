package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.Baby

interface PersistenceService {
    suspend fun saveSelectedBaby(baby: Baby)
    suspend fun isSelectedBaby(baby: Baby): Boolean
}