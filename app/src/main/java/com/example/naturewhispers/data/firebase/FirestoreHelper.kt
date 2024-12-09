package com.example.naturewhispers.data.firebase

import com.example.naturewhispers.data.firebase.IFirestoreHelper.Response
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class FirestoreHelper: IFirestoreHelper {

    private val dataKey = "data"
    private val firestore = Firebase.firestore
    private val gson = Gson()


    override suspend fun <T : Any> saveData(userKey: String, data: List<T>, collectionPath: CollectionPath): Boolean {
        return try {
            firestore.collection(userKey).document(collectionPath.path).set(mapOf(dataKey to data)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun <T : Any> getData(userKey: String, collectionPath: CollectionPath, clazz: Class<T>): Response<List<T>> {
        return try {
            if (!doesDocumentExist(userKey, collectionPath)) {
                return Response.Error("No data found for $userKey")
            }
            val document = firestore.collection(userKey).document(collectionPath.path).get().await()
            val dataList = document.get(dataKey) as? List<*>
            val dataMap = dataList?.mapNotNull { dataMap ->
                // Convert each map to JSON, then parse to an object of type T
                val json = gson.toJson(dataMap)
                gson.fromJson(json, clazz)
            }
             if (!dataMap.isNullOrEmpty())
                return Response.Success(dataMap)
            else Response.Error("No data found")
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun doesDocumentExist(userKey: String, collectionPath: CollectionPath): Boolean {
        return try {
            val documentSnapshot = firestore.collection(userKey).document(collectionPath.path).get().await()
            documentSnapshot.exists()  // Returns true if the document exists, false otherwise
        } catch (e: Exception) {
            e.printStackTrace()
            false  // Handle the exception and return false if there's an error
        }
    }
}
