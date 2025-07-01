package com.biblio.teque.domain.model

data class Book(val title: String, val author: String) {
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(author.isNotBlank()) { "Author cannot be blank" }
    }
}
