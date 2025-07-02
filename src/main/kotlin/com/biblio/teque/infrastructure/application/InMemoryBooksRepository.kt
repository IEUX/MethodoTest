package com.biblio.teque.infrastructure.application

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
class InMemoryBooksRepository : BooksRepository {
    private val books = mutableListOf<Book>()

    override fun save(book: Book) {
        books.add(book)
    }

    override fun findAll(): List<Book> {
        return books.sortedBy { it.title }
    }
}
