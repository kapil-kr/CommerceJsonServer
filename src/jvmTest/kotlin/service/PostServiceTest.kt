package service

import PostItem
import ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PostServiceTest : ServerTest() {

    private val postService = PostService()

    @Test
    fun getPosts(): Unit = runBlocking {
        val post1 = PostItem("test1", "test1Author",1, 1)
        val post2 = PostItem("test2", "test2Author",2,2)
        postService.addPost(post1)
        postService.addPost(post2)

        val postsFromFile = postService.getPosts(null, null, null, null)
        assertThat(postsFromFile).hasSize(2)
        assertThat(postsFromFile).extracting("title").containsExactlyInAnyOrder(post1.title, post2.title)
        assertThat(postsFromFile).extracting("author").containsExactlyInAnyOrder(post1.author, post2.author)
        assertThat(postsFromFile).extracting("views").containsExactlyInAnyOrder(post1.views, post2.views)
        assertThat(postsFromFile).extracting("reviews").containsExactlyInAnyOrder(post1.reviews, post2.reviews)
    }

    @Test
    fun addPost(): Unit = runBlocking {
        val post1 = PostItem("test1", "test1Author",1, 1)
        postService.addPost(post1)
        val postFromFile = postService.getPosts(null, null, null, null)
        assertThat(postFromFile).hasSize(1)
        assertThat(postFromFile.get(0).title).isEqualTo(post1.title)
        assertThat(postFromFile.get(0).author).isEqualTo(post1.author)
        assertThat(postFromFile.get(0).views).isEqualTo(post1.views)
        assertThat(postFromFile.get(0).reviews).isEqualTo(post1.reviews)
    }

    @Test
    fun deletePost(): Unit = runBlocking {
        val post1 = PostItem("test1", "test1Author",1, 1)
        postService.addPost(post1)

        val postFromFile = postService.getPosts(null, null, null, null)
        assertThat(postFromFile).hasSize(1)
        postService.deletePost(postFromFile.get(0).id)

        assertThat(postService.getPosts(null, null, null, null)).isEmpty()
    }

    @Test
    fun updatePost(): Unit = runBlocking {
        val post = PostItem("test1", "test1Author",1, 1)
        val newPost = PostItem("test2", "test2Author",2,2)
        postService.addPost(post)

        val postFromFile = postService.getPosts(null, null, null, null)
        assertThat(postFromFile).hasSize(1)
        postService.updatePost(postFromFile.get(0).id, newPost)

        val updatedPostFromFile = postService.getPosts(null, null, null, null)
        assertThat(postFromFile).hasSize(1)
        assertThat(updatedPostFromFile.get(0).title).isEqualTo(newPost.title)
        assertThat(updatedPostFromFile.get(0).author).isEqualTo(newPost.author)
        assertThat(updatedPostFromFile.get(0).views).isEqualTo(newPost.views)
        assertThat(updatedPostFromFile.get(0).reviews).isEqualTo(newPost.reviews)
    }
}