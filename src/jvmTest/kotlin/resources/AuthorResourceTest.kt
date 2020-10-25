package resources

import AuthorItem
import ServerTest
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlinx.coroutines.runBlocking
import model.Author
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorResourceTest: ServerTest() {
    @Test
    fun testGetAuthors(): Unit = runBlocking {
        val author1 = AuthorItem("test1", "test1Author",1)
        val author2 = AuthorItem("test2", "test2Author",2)
        addAuthor(author1)
        addAuthor(author2)
        val authorList = RestAssured.get("/authors")
                .then()
                .statusCode(200)
                .extract().to<List<Author>>()

        assertThat(authorList).hasSize(2)
        assertThat(authorList).extracting("first_name").containsExactlyInAnyOrder(author1.first_name, author2.first_name)
        assertThat(authorList).extracting("last_name").containsExactlyInAnyOrder(author1.last_name, author2.last_name)
        assertThat(authorList).extracting("posts").containsExactlyInAnyOrder(author1.posts, author2.posts)
    }

    @Test
    fun testUpdateAuthor(): Unit = runBlocking {
        val author1 = AuthorItem("test1", "test1Author",1)
        addAuthor(author1)
        val authorList = RestAssured.get("/authors")
            .then()
            .statusCode(200)
            .extract().to<List<Map<*,*>>>()

        assertThat(authorList).hasSize(1)
        assertThat(authorList).extracting("first_name").containsOnly(author1.first_name)
        assertThat(authorList).extracting("last_name").containsOnly(author1.last_name)
        assertThat(authorList).extracting("posts").containsOnly(author1.posts)

        val newAuthor = AuthorItem( "test2", "test2Author", 1)
        given()
            .contentType(ContentType.JSON)
            .body(newAuthor)
            .When()
            .put("/authors/${authorList.get(0).get("id")}")
            .then()
            .statusCode(200)

        val authorListAfterUpdate = RestAssured.get("/authors")
                .then()
                .statusCode(200)
                .extract().to<List<AuthorItem>>()

        assertThat(authorListAfterUpdate).hasSize(1)
        assertThat(authorListAfterUpdate).extracting("first_name").containsOnly(newAuthor.first_name)
        assertThat(authorListAfterUpdate).extracting("last_name").containsOnly(newAuthor.last_name)
        assertThat(authorListAfterUpdate).extracting("posts").containsOnly(newAuthor.posts)
    }

    @Test
    fun testDeleteAuthor(): Unit = runBlocking{

        val author1 = AuthorItem("test1", "test1Author",1)
        addAuthor(author1)
        val authorList = RestAssured.get("/authors")
            .then()
            .statusCode(200)
            .extract().to<List<Map<*,*>>>()

        assertThat(authorList).hasSize(1)
        assertThat(authorList).extracting("first_name").containsOnly(author1.first_name)
        assertThat(authorList).extracting("last_name").containsOnly(author1.last_name)
        assertThat(authorList).extracting("posts").containsOnly(author1.posts)

        RestAssured.delete("/authors/${authorList.get(0).get("id")}")
                .then()
                .statusCode(200)

        val authorListAfterDelete = RestAssured.get("/authors")
                .then()
                .statusCode(200)
                .extract().to<List<Author>>()
        assertThat(authorListAfterDelete).hasSize(0)
    }

    private fun addAuthor(author: AuthorItem) {
        given().contentType(ContentType.JSON)
                .body(author).When().post("/authors").then()
                .statusCode(201)
    }
}