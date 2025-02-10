package com.example.naturewhispers.data.cloud

interface IFirestoreHelper {

    suspend fun <T : Any> saveData(userKey: String, data: List<T>, collectionPath: CollectionPath): Boolean

    // Generic method to fetch all data from a user's collection
    suspend fun <T : Any> getData(userKey: String, collectionPath: CollectionPath, clazz: Class<T>): Response<List<T>>

    sealed class Response<out T> {
        data class Success<out T>(val data: T) : Response<T>()
        data class Error(val message: String) : Response<Nothing>()
    }
}

enum class CollectionPath(val path: String) {
    PRESETS("presets"),
    STATS("stats")
}