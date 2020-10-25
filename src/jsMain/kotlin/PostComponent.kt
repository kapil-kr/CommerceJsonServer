import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement

private val scope = MainScope()

val PostComponent = functionalComponent<RProps> { _ ->
    val (postList, setPostList) = useState(emptyList<PostItem>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setPostList(getPostList())
        }
    }

    h1 {
        +"Posts"
    }

    val (title, setTitle) = useState("")
    val (author, setAuthor) = useState("")
    val (views, setViews) = useState(0)
    val (reviews, setReviews) = useState(0)

    ul {
        postList.forEach { item ->
            li {
                key = item.toString()
                +"[Title: ${item.title}] [Author: ${item.author}] [Views: ${item.views}] [Reviews: ${item.reviews}]"
                button {
                    +"Delete"
                    attrs.onClickFunction = {
                        scope.launch {
                            deletePostItem(item)
                            setPostList(getPostList())
                        }
                    }
                }
                form {
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value
                            setTitle(value)
                        }
                    }
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value
                            setAuthor(value)
                        }
                    }
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value.toInt()
                            setViews(value)
                        }
                    }
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value.toInt()
                            setReviews(value)
                        }
                    }
                    button {
                        +"Update"
                        attrs.onClickFunction = {
                            it.preventDefault()
                            val cartItem = PostItem(title, author, views, reviews)
                            scope.launch {
                                updatePostItem(item, cartItem)
                                setPostList(getPostList())
                            }
                        }
                    }
                }
            }
        }
    }
    h3{
        +"Insert Posts "
    }

    form {
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setTitle(value)
            }
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setAuthor(value)
            }
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value.toInt()
                setViews(value)
            }
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value.toInt()
                setReviews(value)
            }
        }
        button {
            +"Insert"
            attrs.onClickFunction = {
                it.preventDefault()
                val cartItem = PostItem(title, author, views, reviews)
                scope.launch {
                    addPostItem(cartItem)
                    setPostList(getPostList())
                }
            }
        }
    }
    h3{
        +"Search APIs"
    }

    val (searchTitle, setSearchTitle)= useState("")
    val (searchAuthor, setSearchAuthor)= useState("")

    form{
        label {
            +"Title "
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setSearchTitle(value)
            }
        }
        label {
            +"Author "
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setSearchAuthor(value)
            }
        }
        button {
            +"Search"
            attrs.onClickFunction = {
                it.preventDefault()
                scope.launch {
                    setPostList(searchPosts(searchTitle, searchAuthor))
                }
            }
        }
        button {
            +"clear"
            attrs.onClickFunction = {
                it.preventDefault()
                scope.launch {
                    setPostList(getPostList())
                }
            }
        }
    }
}