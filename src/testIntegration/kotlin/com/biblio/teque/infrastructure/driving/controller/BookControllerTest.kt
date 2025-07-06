package com.biblio.teque.infrastructure.driving.controller

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.usecase.ManageBooksUseCase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(BookController::class)
class BookControllerTest(
    @Autowired private val mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var service: ManageBooksUseCase

    @Test
    fun `GET books returns expected payload`() {
        every { service.listBooks() } returns listOf(Book("1984", "George Orwell"))

        mockMvc.perform(
            get("/books").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    [
                      {"title":"1984","author":"George Orwell","isRented":false}
                    ]
                    """.trimIndent(),
                    true
                )
            )
    }

    @Test
    fun `POST rent returns 200 when book available`() {
        every { service.rentBook("Dune") } returns Unit

        mockMvc.perform(
            post("/books/Dune/rent").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(exactly = 1) { service.rentBook("Dune") }
    }

    @Test
    fun `POST rent returns 409 when book already rented`() {
        every { service.rentBook("Dune") } throws IllegalStateException("Already rented")

        mockMvc.perform(
            post("/books/Dune/rent").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict)
    }

    @Test
    fun `POST rent returns 404 when book not found`() {
        every { service.rentBook("Unknown") } throws IllegalArgumentException("Not found")

        mockMvc.perform(
            post("/books/Unknown/rent").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `POST rent returns 500 on unexpected error`() {
        every { service.rentBook("Dune") } throws RuntimeException("Boom")

        mockMvc.perform(
            post("/books/Dune/rent").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError)
    }
}
