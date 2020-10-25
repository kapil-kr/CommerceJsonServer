package resources

import AuthorItem
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import service.AuthorService
import java.lang.IllegalArgumentException

fun Route.author(authorService: AuthorService) {

    route(AuthorItem.path) {
        authenticate("bookAuth") {
            get {
                val firstName = call.request.queryParameters["first_name"]
                val lastName = call.request.queryParameters["last_name"]
                val sort = call.request.queryParameters["_sort"]
                val order = call.request.queryParameters["_order"]
                try {
                    call.respond(authorService.getAuthors(firstName, lastName, sort, order))
                }
                catch (e: Exception) {
                    error(e.message!!)
                }
            }
            get("/{id}") {
                val id = call.parameters["id"]?: error("Invalid request")
                val author = authorService.getAuthorById(id)
                if(author == null) {
                    call.respond(HttpStatusCode.NotFound, "this Author doesn't exists")
                }
                else {
                    call.respond(author)
                }
            }
            post {
                try {
                    val newAuthor = call.receive<AuthorItem>()
                    if(newAuthor.first_name.trim().isEmpty() || newAuthor.last_name.trim().isEmpty())
                        call.respond(HttpStatusCode.BadRequest, "first Name and Last name can't be empty")
                    else {
                        authorService.addAuthor(newAuthor)
                        call.respond(HttpStatusCode.Created)
                    }
                }
                catch (e: Exception) {
                    when(e) {
                        is kotlinx.serialization.SerializationException -> call.respond(HttpStatusCode.BadRequest, e.message!!)
                        else -> error(e.message!!)
                    }
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: error("Invalid delete request")
                val author = authorService.getAuthorById(id)
                if(author == null) {
                    call.respond(HttpStatusCode.NotFound, "this Author doesn't exists")
                }
                else {
                    try {
                        authorService.deleteAuthor(id)
                        call.respond(HttpStatusCode.OK)
                    }
                    catch (e: Exception) {
                        error(e.message!!)
                    }
                }
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toString() ?: error("Invalid Update request")
                val author = authorService.getAuthorById(id)
                if(author == null) {
                    call.respond(HttpStatusCode.NotFound, "this Author doesn't exists")
                }
                else {
                    try {
                        authorService.updateAuthor(id, call.receive<AuthorItem>())
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        when(e) {
                            is kotlinx.serialization.SerializationException -> call.respond(HttpStatusCode.BadRequest, e.message!!)
                            else -> error(e.message!!)
                        }
                    }
                }
            }
            patch("/{id}") {
                val id = call.parameters["id"]?: error("Invalid Update request")
                val author = authorService.getAuthorById(id)
                if(author == null) {
                    call.respond(HttpStatusCode.NotFound, "this Author doesn't exists")
                }
                else {
                    try {
                        val mapper = jacksonObjectMapper()
                        val partialAuthor: MutableMap<Any, Any> = mapper.readValue(call.receive<String>())
                        if(authorService.isValidPatch(partialAuthor)) {
                            authorService.patchAuthor(id, partialAuthor)
                            call.respond(HttpStatusCode.OK)
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is IllegalAccessException ->
                                call.respond(HttpStatusCode.MethodNotAllowed, e.message!!)
                            is NoSuchFieldException ->
                                call.respond(HttpStatusCode.BadRequest)
                            is IllegalArgumentException ->
                                call.respond(HttpStatusCode.BadRequest, e.message!!)
                            else ->
                                error(e.message!!)
                        }
                    }
                }
            }
        }
    }
}