package com.example.naturewhispers.data.cloud

class FirestoreHelperFake: IFirestoreHelper  {

    private val cloud = mutableMapOf<String, Any>()

    override suspend fun <T : Any> saveData(
        userKey: String,
        data: List<T>,
        collectionPath: CollectionPath
    ): Boolean {
        cloud["$userKey$collectionPath"] = data
        return true
    }

    override suspend fun <T : Any> getData(
        userKey: String,
        collectionPath: CollectionPath,
        clazz: Class<T>
    ): IFirestoreHelper.Response<List<T>> {
        return IFirestoreHelper.Response.Success(cloud["$userKey$collectionPath"] as List<T>)
    }
}