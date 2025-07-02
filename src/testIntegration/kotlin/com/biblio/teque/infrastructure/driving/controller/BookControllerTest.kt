package com.biblio.teque.infrastructure.driving.controller

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.usecase.ManageBooksUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var manageBooksUseCase: ManageBooksUseCase

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `GET books should return sorted book list`() {
        val books = listOf(
            Book("1984", "George Orwell"),
            Book("Brave New World", "Aldous Huxley")
        )
        every { manageBooksUseCase.listBooks() } returns books.sortedBy { it.title }

        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].title").value("1984"))
            .andExpect(jsonPath("$[1].title").value("Brave New World"))

        verify(exactly = 1) { manageBooksUseCase.listBooks() }
    }

    @Test
    fun `POST books should create book when input is valid`() {
        val payload = mapOf("title" to "Dune", "author" to "Frank Herbert")

        every { manageBooksUseCase.addBook(any()) } returns Unit

        mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated)

        verify(exactly = 1) {
            manageBooksUseCase.addBook(Book("Dune", "Frank Herbert"))
        }
    }

    @Test
    fun `POST books should return 400 when input is invalid`() {
        val payload = mapOf("title" to "", "author" to "")

        // Le domaine va lancer une IllegalArgumentException
        every { manageBooksUseCase.addBook(any()) } throws IllegalArgumentException("Title and author must not be empty")

        mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Title and author must not be empty"))
    }

    @Test
    fun `POST books should return 500 on unexpected error`() {
        val payload = mapOf("title" to "X", "author" to "Y")

        every { manageBooksUseCase.addBook(any()) } throws RuntimeException("Unexpected error")

        mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().is5xxServerError)
    }
}
