import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement

private val scope = MainScope()

val AuthorComponent = functionalComponent<RProps> { _ ->
    val (authorList, setAuthorList) = useState(emptyList<AuthorItem>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setAuthorList(getAuthorList())
        }
    }

    h1 {
        +"Authors"
    }
    val (firstName, setFirstName) = useState("")
    val (lastName, setLastName) = useState("")
    val (posts, setPosts) = useState(0)

    ul {
        authorList.forEach { item ->
            li {
                key = item.toString()
                +"[First Name: ${item.first_name}] [Last Name: ${item.last_name}] [Posts: ${item.posts}]"
                button {
                    +"Delete"
                    attrs.onClickFunction = {
                        scope.launch {
                            deleteAuthorItem(item)
                            setAuthorList(getAuthorList())
                        }
                    }
                }
                form {
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value
                            setFirstName(value)
                        }
                    }
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value
                            setLastName(value)
                        }
                    }
                    input(InputType.text) {
                        attrs.onChangeFunction = {
                            val value = (it.target as HTMLInputElement).value.toInt()
                            setPosts(value)
                        }
                    }
                    button {
                        +"Update"
                        attrs.onClickFunction = {
                            it.preventDefault()
                            val cartItem = AuthorItem(firstName, lastName, posts)
                            scope.launch {
                                updateAuthorItem(item, cartItem)
                                setAuthorList(getAuthorList())
                            }
                        }
                    }
                }
            }
        }
    }
    h3{
        +"Insert Author"
    }

    form {
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setFirstName(value)
            }
        }
        input(InputType.email) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setLastName(value)
            }
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value.toInt()
                setPosts(value)
            }
        }
        button {
            +"Insert"
            attrs.onClickFunction = {
                it.preventDefault()
                val cartItem = AuthorItem(firstName, lastName, posts)
                scope.launch {
                    addAuthorItem(cartItem)
                    setAuthorList(getAuthorList())
                }
            }
        }
    }
    h3{
        +"Search APIs"
    }

    val (searchFName, setSearchFName)= useState("")
    val (searchLName, setSearchLName)= useState("")
    form{
        label {
            +"First Name: "
        }
        input(InputType.text) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setSearchFName(value)
            }
        }
        label {
            +"Last Name: "
        }
        input(InputType.email) {
            attrs.onChangeFunction = {
                val value = (it.target as HTMLInputElement).value
                setSearchLName(value)
            }
        }
        button {
            +"Search"
            attrs.onClickFunction = {
                it.preventDefault()
                scope.launch {
                    setAuthorList(searchAuthor(searchFName, searchLName))
                }
            }
        }
        button {
            +"clear"
            attrs.onClickFunction = {
                it.preventDefault()
                scope.launch {
                    setAuthorList(getAuthorList())
                }
            }
        }
    }
}