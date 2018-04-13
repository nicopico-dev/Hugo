package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.Baby

interface PersistenceService {
    suspend fun readBaby(): Baby?
    suspend fun saveBaby(baby: Baby)
}