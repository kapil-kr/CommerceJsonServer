package resources

import PostItem
import ServerTest
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlinx.coroutines.runBlocking
import model.Post
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PostResourceTest: ServerTest() {
    @Test
    fun testGetPosts(): Unit = runBlocking {
        val post1 = PostItem("test1", "test1Author",1, 1)
        val post2 = PostItem("test2", "test2Author",2,2)
        addPost(post1)
        addPost(post2)
        val postList = RestAssured.get("/posts")
                .then()
                .statusCode(200)
                .extract().to<List<Post>>()

        assertThat(postList).hasSize(2)
        assertThat(postList).extracting("title").containsExactlyInAnyOrder(post1.title, post2.title)
        assertThat(postList).extracting("author").containsExactlyInAnyOrder(post1.author, post2.author)
        assertThat(postList).extracting("views").containsExactlyInAnyOrder(post1.views, post2.views)
        assertThat(postList).extracting("reviews").containsExactlyInAnyOrder(post1.reviews, post2.reviews)
    }

    @Test
    fun testUpdatePost(): Unit = runBlocking {
        val post1 = PostItem("test1", "test1Author",1, 1)
        addPost(post1)
        val postList = RestAssured.get("/posts")
            .then()
            .statusCode(200)
            .extract().to<List<Map<*,*>>>()

        assertThat(postList).hasSize(1)
        assertThat(postList).extracting("title").containsOnly(post1.title)
        assertThat(postList).extracting("author").containsOnly(post1.author)
        assertThat(postList).extracting("views").containsOnly(post1.views)
        assertThat(postList).extracting("reviews").containsOnly(post1.reviews)

        val newPost = PostItem( "test2", "test2Author", 1, 1)
        given()
            .contentType(ContentType.JSON)
            .body(newPost)
            .When()
            .put("/posts/${postList.get(0).get("id")}")
            .then()
            .statusCode(200)

        val postListAfterUpdate = RestAssured.get("/posts")
                .then()
                .statusCode(200)
                .extract().to<List<PostItem>>()

        assertThat(postListAfterUpdate).hasSize(1)
        assertThat(postListAfterUpdate).extracting("title").containsOnly(newPost.title)
        assertThat(postListAfterUpdate).extracting("author").containsOnly(newPost.author)
        assertThat(postListAfterUpdate).extracting("views").containsOnly(newPost.views)
        assertThat(postListAfterUpdate).extracting("reviews").containsOnly(newPost.reviews)
    }

    @Test
    fun testDeletePost(): Unit = runBlocking{

        val post1 = PostItem("test1", "test1Author",1, 1)
        addPost(post1)
        val postList = RestAssured.get("/posts")
            .then()
            .statusCode(200)
            .extract().to<List<Map<*,*>>>()

        assertThat(postList).hasSize(1)
        assertThat(postList).extracting("title").containsOnly(post1.title)
        assertThat(postList).extracting("author").containsOnly(post1.author)
        assertThat(postList).extracting("views").containsOnly(post1.views)
        assertThat(postList).extracting("reviews").containsOnly(post1.reviews)

        RestAssured.delete("/posts/${postList.get(0).get("id")}")
                .then()
                .statusCode(200)

        val postListAfterDelete = RestAssured.get("/posts")
                .then()
                .statusCode(200)
                .extract().to<List<Post>>()
        assertThat(postListAfterDelete).hasSize(0)
    }

    private fun addPost(post: PostItem) {
        given().contentType(ContentType.JSON)
                .body(post).When().post("/posts").then()
                .statusCode(201)
    }
}