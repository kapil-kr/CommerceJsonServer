package resources

import PostItem
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import service.PostService
import java.lang.IllegalArgumentException

fun Route.posts(postService: PostService) {

    route(PostItem.path) {
        authenticate("bookAuth") {
            get {
                val title = call.request.queryParameters["title"]
                val author = call.request.queryParameters["author"]
                val sort = call.request.queryParameters["_sort"]
                val order = call.request.queryParameters["_order"]
                try {
                    call.respond(postService.getPosts(title, author, sort, order))
                }
                catch (e: Exception) {
                    error(e.message!!)
                }
            }
            get("/{id}") {
                val id = call.parameters["id"]?: error("Invalid request")
                val post = postService.getPostById(id)
                if(post == null) {
                    call.respond(HttpStatusCode.NotFound, "this Post doesn't exists")
                }
                else {
                    call.respond(post)
                }
            }
            post {
                try {
                    val newPost = call.receive<PostItem>()
                    if(newPost.title.trim().isEmpty() || newPost.author.trim().isEmpty())
                        call.respond(HttpStatusCode.BadRequest, "Title and author can't be empty")
                    else {
                        postService.addPost(newPost)
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
                val post = postService.getPostById(id)
                if(post == null) {
                    call.respond(HttpStatusCode.NotFound, "this post doesn't exists")
                }
                else {
                    try {
                        postService.deletePost(id)
                        call.respond(HttpStatusCode.OK)
                    }
                    catch (e: Exception) {
                        error(e.message!!)
                    }
                }
            }
            put("/{id}") {
                val id = call.parameters["id"]?: error("Invalid Update request")
                val post = postService.getPostById(id)
                if(post == null) {
                    call.respond(HttpStatusCode.NotFound, "this post doesn't exists")
                }
                else {
                    try {
                        postService.updatePost(id, call.receive<PostItem>())
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
                val post = postService.getPostById(id)
                if(post == null) {
                    call.respond(HttpStatusCode.NotFound, "this post doesn't exists")
                }
                else {
                    try {
                        val mapper = jacksonObjectMapper()
                        val partialPost: MutableMap<Any, Any> = mapper.readValue(call.receive<String>())
                        if(postService.isValidPatch(partialPost)) {
                            postService.patchPost(id, partialPost)
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