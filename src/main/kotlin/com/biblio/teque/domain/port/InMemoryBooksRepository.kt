package com.biblio.teque.domain.port

import com.biblio.teque.domain.model.Book
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryBooksRepository : BooksRepository {
    private val books = CopyOnWriteArrayList<Book>()

    override fun save(book: Book) {
        books += book
    }

    override fun findAll(): List<Book> = books.toList()
}
