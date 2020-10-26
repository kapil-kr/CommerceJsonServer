package service

import PostItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.BookStore
import model.Post
import java.io.File

class PostService {

    fun getFile(): File {
        return File(this::class.java.classLoader.getResource("store.json").path)
    }

    fun getPosts(title: String?, author: String?, sort: String?, order: String?): List<Post>  {
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        val ans =  store.posts.filter {
            (if(title != null && title !="")
                it.title == title
            else
                true)
                        &&
                        (if(author != null && author != "")
                it.author == author
            else true)
        }
        .sortedBy {
//            Post::class.java.getDeclaredField(sort.toString())
            when (sort) {
                "views" -> it.views
                "reviews" -> it.reviews
                else -> it.views
            }
        }
        return if(order == "asc")
            ans
        else
            ans.reversed()
    }

    fun addPost(newPostItem: PostItem) {
        val newPost = Post(newPostItem.title, newPostItem.author, newPostItem.views, newPostItem.reviews)
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        store.posts.add(newPost)
        getFile().writeText(Json.encodeToString(store))
    }

    fun deletePost(id: String) {
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        store.posts.removeIf {
            it.id == id
        }
        getFile().writeText(Json.encodeToString(store))
    }

    fun getPostById(id: String): Post? {
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        store.posts.forEach {
            if(it.id == id)
                return it
        }
        return null
    }

    fun updatePost(id: String, newPost: PostItem) : Boolean {
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        store.posts.forEach {
            if(it.id == id) {
                it.title = newPost.title
                it.reviews = newPost.reviews
                it.author = newPost.author
                it.views = newPost.views
            }
        }
        getFile().writeText(Json.encodeToString(store))
        return true;
    }

    fun isValidPatch(newPost: Map<Any, Any>) : Boolean {
        newPost.forEach { k, v ->
            if(k.toString() == "id")
                throw IllegalAccessException("Id field cannot be changed")
            Post::class.java.getDeclaredField(k.toString())
        }
        return true
    }

    fun patchPost(id: String, newPost: Map<Any, Any>): Boolean {
        val store = Json.decodeFromString<BookStore>(getFile().readText())
        store.posts.forEach { post ->
            if(post.id == id) {
                newPost.forEach {
                    Post::class.java.getDeclaredField(it.key.toString()).set(post, it.value)
                }
            }
        }
        getFile().writeText(Json.encodeToString(store))
        return true;
    }
}