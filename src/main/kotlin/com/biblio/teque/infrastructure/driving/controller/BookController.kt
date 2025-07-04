package com.biblio.teque.infrastructure.driving.controller

import com.biblio.teque.infrastructure.driving.controller.dto.BookDTO
import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.usecase.ManageBooksUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val manageBooksUseCase: ManageBooksUseCase
) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return manageBooksUseCase.listBooks()
            .map { BookDTO(it.title, it.author) }
    }

    @PostMapping
    fun createBook(@RequestBody dto: BookDTO): ResponseEntity<String> {
        return try {
            val book = Book(dto.title, dto.author)
            manageBooksUseCase.addBook(book)
            ResponseEntity.status(HttpStatus.CREATED).body("Book created.")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }

    //@ResponseStatus()
}
