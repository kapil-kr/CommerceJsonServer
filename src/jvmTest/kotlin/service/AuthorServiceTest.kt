package service

import AuthorItem
import ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorServiceTest : ServerTest() {

    private val authorService = AuthorService()

    @Test
    fun getAuthors(): Unit = runBlocking {
        val author1 = AuthorItem("test1", "test1Author",1)
        val author2 = AuthorItem("test2", "test2Author",2)
        authorService.addAuthor(author1)
        authorService.addAuthor(author2)

        val authorsFromFile = authorService.getAuthors(null, null, null, null)
        assertThat(authorsFromFile).hasSize(2)
        assertThat(authorsFromFile).extracting("first_name").containsExactlyInAnyOrder(author1.first_name, author2.first_name)
        assertThat(authorsFromFile).extracting("last_name").containsExactlyInAnyOrder(author1.last_name, author2.last_name)
        assertThat(authorsFromFile).extracting("posts").containsExactlyInAnyOrder(author1.posts, author2.posts)
    }

    @Test
    fun addAuthor(): Unit = runBlocking {
        val author1 = AuthorItem("test1", "test1Author",1)
        authorService.addAuthor(author1)
        val authorFromFile = authorService.getAuthors(null, null, null, null)
        assertThat(authorFromFile).hasSize(1)
        assertThat(authorFromFile.get(0).first_name).isEqualTo(author1.first_name)
        assertThat(authorFromFile.get(0).last_name).isEqualTo(author1.last_name)
        assertThat(authorFromFile.get(0).posts).isEqualTo(author1.posts)
    }

    @Test
    fun deleteAuthor(): Unit = runBlocking {
        val author1 = AuthorItem("test1", "test1Author",1)
        authorService.addAuthor(author1)

        val authorFromFile = authorService.getAuthors(null, null, null, null)
        assertThat(authorFromFile).hasSize(1)
        authorService.deleteAuthor(authorFromFile.get(0).id)

        assertThat(authorService.getAuthors(null, null, null, null)).isEmpty()
    }

    @Test
    fun updateAuthor(): Unit = runBlocking {
        val author = AuthorItem("test1", "test1Author",1)
        val newAuthor = AuthorItem("test2", "test2Author",2)
        authorService.addAuthor(author)

        val authorFromFile = authorService.getAuthors(null, null, null, null)
        assertThat(authorFromFile).hasSize(1)
        authorService.updateAuthor(authorFromFile.get(0).id, newAuthor)

        val updatedAuthorFromFile = authorService.getAuthors(null, null, null, null)
        assertThat(authorFromFile).hasSize(1)
        assertThat(updatedAuthorFromFile.get(0).first_name).isEqualTo(newAuthor.first_name)
        assertThat(updatedAuthorFromFile.get(0).last_name).isEqualTo(newAuthor.last_name)
        assertThat(updatedAuthorFromFile.get(0).posts).isEqualTo(newAuthor.posts)
    }
}