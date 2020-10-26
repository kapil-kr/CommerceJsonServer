package service

import java.io.File

object FileFactory {
    fun init() {
        val path = this::class.java.classLoader.getResource("store.json").path
        if(!File(path).exists()) {
            val file = File(path)
            file.writeText("{\"posts\":[],\"authors\":[]}")
        }
    }
}