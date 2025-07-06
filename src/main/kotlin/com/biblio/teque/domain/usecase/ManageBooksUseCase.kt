package com.biblio.teque.domain.usecase

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository

class ManageBooksUseCase(private val repository: BooksRepository) {

    fun addBook(book: Book): Unit {
        repository.save(book)
    }

    fun listBooks(): List<Book> =
        repository.findAll().sortedBy { it.title.lowercase()
    }

    fun rentBook(title: String) {
        val book = repository.findByTitle(title)
            ?: throw IllegalArgumentException("Book '$title' not found")

        if (book.isRented)
            throw IllegalStateException("Book '$title' is already rented")

        book.isRented = true
        repository.save(book)          // persiste l’état loué
    }
}
