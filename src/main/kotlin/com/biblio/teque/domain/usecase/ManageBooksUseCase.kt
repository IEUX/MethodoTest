package com.biblio.teque.domain.usecase

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository

class ManageBooksUseCase(private val repository: BooksRepository) {

    /**
     * Adds a new [Book] to the repository after validating that the title and author are not blank.
     * @throws IllegalArgumentException if the title or author is blank.
     */
    fun addBook(title: String, author: String): Book {
        val book = Book(title, author)
        repository.save(book)
        return book
    }

    fun listBooks(): List<Book> =
        repository.findAll().sortedBy { it.title.lowercase() }
}
