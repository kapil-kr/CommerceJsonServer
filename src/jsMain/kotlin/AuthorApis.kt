import io.ktor.http.*
import io.ktor.client.request.*

suspend fun getAuthorList(): List<AuthorItem> {
    return jsonClient.get(endpoint + AuthorItem.path)
}

suspend fun addAuthorItem(aItem: AuthorItem) {
    jsonClient.post<Unit>(endpoint + AuthorItem.path) {
        contentType(ContentType.Application.Json)
        body = aItem
    }
}

suspend fun deleteAuthorItem(aItem: AuthorItem) {
    jsonClient.delete<Unit>(endpoint + AuthorItem.path + "/${aItem.id}")
}

suspend fun updateAuthorItem(aItem: AuthorItem, newItem: AuthorItem) {
    jsonClient.put<Unit>(endpoint + AuthorItem.path + "/${aItem.id}") {
        contentType(ContentType.Application.Json)
        body = newItem
    }
}

suspend fun searchAuthor(fName: String, lName: String): List<AuthorItem> {
    return jsonClient.get(endpoint + AuthorItem.path + "?first_name=$fName&last_name=$lName")
}
