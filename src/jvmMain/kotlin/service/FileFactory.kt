package service

import java.io.File

object FileFactory {
    fun init() {
        if(!File("/store.json").exists()) {
            val file = File("/store.json")
            file.writeText("{\"posts\":[],\"authors\":[]}")
        }
    }
}