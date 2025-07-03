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
import org.springframework.test.web.servlet.get
import com.ninjasquad.springmockk.MockkBean        // @MockkBean annotation
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest
class BookControllerTest(
    @Autowired private val mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var service: ManageBooksUseCase

    @Test
    fun `GET books returns expected payload`() {   
    every {service.listBooks()} returns listOf(Book("1984", "George Orwell"))
    mockMvc.get("/books") {
        accept = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                json(
                    """
                    [
                        {"title":"1984","author":"George Orwell"}
                    ]
                    """.trimIndent(),
                    /* strict = */ true  // set to false if order/extra fields shouldn’t fail the test
                )
            }
        }
        verify { service.listBooks() }
    }
}
