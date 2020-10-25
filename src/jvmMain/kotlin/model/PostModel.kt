package model

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.RandomStringUtils

@Serializable
data class Post(
        @JvmField var title: String,
        @JvmField var author: String,
        @JvmField var views: Int,
        @JvmField var reviews: Int,
        val id: String = RandomStringUtils.randomAlphanumeric(8)) {
    companion object {
        const val path = "/posts"
    }
}

@Serializable
data class Author(
    @JvmField var first_name: String,
    @JvmField var last_name: String,
    @JvmField var posts: Int,
    val id: String = RandomStringUtils.randomAlphanumeric(8)) {
    companion object {
        const val path = "/authors"
    }
}

@Serializable
data class BookStore(
    val posts: MutableList<Post>,
    val authors: MutableList<Author>
)