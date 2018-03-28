package fr.nicopico.hugo.domain.model

class Message(val content: String, val type: Type, val transient: Boolean) {

    companion object {
        fun error(content: String, transient: Boolean = true) = Message(content, Type.ERROR, transient)
        fun info(content: String, transient: Boolean = true) = Message(content, Type.INFO, transient)
    }

    enum class Type {
        ERROR,
        INFO
    }
}