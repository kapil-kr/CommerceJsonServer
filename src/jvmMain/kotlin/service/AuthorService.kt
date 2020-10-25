package service

import AuthorItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Author
import model.BookStore
import java.io.File

class AuthorService {

    private val file = File("store.json")

    fun getAuthors(firstName: String?, lastName: String?, sort: String?, order: String?): List<Author>  {
        val store = Json.decodeFromString<BookStore>(file.readText())
        val ans =  store.authors.filter {
            (if(firstName != null && firstName != "")
                it.first_name == firstName
            else
                true)
                        &&
                        (if(lastName != null && lastName != "")
                it.last_name == lastName
            else true)
        }
        .sortedBy {
            it.posts
        }
        return if(order == "asc")
            ans
        else
            ans.reversed()
    }

    fun addAuthor(newAuthorItem: AuthorItem) {
        val newAuthor = Author(newAuthorItem.first_name, newAuthorItem.last_name, newAuthorItem.posts)
        val store = Json.decodeFromString<BookStore>(file.readText())
        store.authors.add(newAuthor)
        file.writeText(Json.encodeToString(store))
    }

    fun deleteAuthor(id: String) {
        val store = Json.decodeFromString<BookStore>(file.readText())
        store.authors.removeIf {
            it.id == id
        }
        file.writeText(Json.encodeToString(store))
    }

    fun getAuthorById(id: String): Author? {
        val store = Json.decodeFromString<BookStore>(file.readText())
        store.authors.forEach {
            if(it.id == id)
                return it
        }
        return null
    }

    fun updateAuthor(id: String, newAuthor: AuthorItem) : Boolean {
        val store = Json.decodeFromString<BookStore>(file.readText())
        store.authors.forEach {
            if(it.id == id) {
                it.first_name = newAuthor.first_name
                it.last_name = newAuthor.last_name
                it.posts = newAuthor.posts
            }
        }
        file.writeText(Json.encodeToString(store))
        return true;
    }

    fun isValidPatch(newAuthor: Map<Any, Any>) : Boolean {
        newAuthor.forEach { k, v ->
            if(k.toString() == "id")
                throw IllegalAccessException("Id field cannot be changed")
            Author::class.java.getDeclaredField(k.toString())
        }
        return true
    }

    fun patchAuthor(id: String, newAuthor: Map<Any, Any>): Boolean {
        val store = Json.decodeFromString<BookStore>(file.readText())
        store.authors.forEach { author ->
            if(author.id == id) {
                newAuthor.forEach {
                    Author::class.java.getDeclaredField(it.key.toString()).set(author, it.value)
                }
            }
        }
        file.writeText(Json.encodeToString(store))
        return true;
    }
}