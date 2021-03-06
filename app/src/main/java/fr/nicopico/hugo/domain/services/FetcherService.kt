package fr.nicopico.hugo.domain.services

interface FetcherService<T> {
    suspend fun get(remoteId: String): T

    fun fetch(fetcher: Fetcher<T>)
    fun stopFetching()

    fun addEntry(entry: T)
    fun updateEntry(entry: T)
    fun removeEntry(entry: T)
}

interface Fetcher<in T> {
    fun onEntryAdded(entry: T)
    fun onEntryModified(entry: T)
    fun onEntryRemoved(entry: T)
    fun onError(exception: Exception)
}