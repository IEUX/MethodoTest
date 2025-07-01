package com.biblio.teque.domain.port

import com.biblio.teque.domain.model.Book

interface BooksRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
}