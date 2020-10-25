import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class PostItem(val title: String, val author: String,
                 val views: Int, val reviews: Int) {
    var id: String = ""
    companion object {
        const val path = "/posts"
    }
}


@Serializable
data class AuthorItem(val first_name: String, val last_name: String,
                    val posts: Int) {
    var id: String = ""
    companion object {
        const val path = "/authors"
    }
}