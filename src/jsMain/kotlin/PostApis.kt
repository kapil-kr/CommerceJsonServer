import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*

import kotlinx.browser.window

val endpoint = window.location.origin

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(Auth) {
        basic {
            realm ="ktor"
            sendWithoutRequest= true
            // remove these hardcoding later
            username = "kapil"
            password = "kapilApp"
        }
    }
}

suspend fun getPostList(): List<PostItem> {
    return jsonClient.get(endpoint + PostItem.path)
}

suspend fun addPostItem(pItem: PostItem) {
    jsonClient.post<Unit>(endpoint + PostItem.path) {
        contentType(ContentType.Application.Json)
        body = pItem
    }
}

suspend fun deletePostItem(pItem: PostItem) {
    jsonClient.delete<Unit>(endpoint + PostItem.path + "/${pItem.id}")
}

suspend fun updatePostItem(pItem: PostItem, newItem: PostItem) {
    jsonClient.put<Unit>(endpoint + PostItem.path + "/${pItem.id}") {
        contentType(ContentType.Application.Json)
        body = newItem
    }
}

suspend fun searchPosts(name: String, author: String): List<PostItem> {
    return jsonClient.get(endpoint + PostItem.path + "?title=$name&author=$author")
}
