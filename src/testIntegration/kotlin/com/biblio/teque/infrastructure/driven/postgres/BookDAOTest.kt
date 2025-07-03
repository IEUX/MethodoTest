package com.biblio.teque.infrastructure.driven.postgres

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import com.biblio.teque.domain.model.Book
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.RowMapper

@Testcontainers
@SpringBootTest
class BookDAOTest {
    // init {
    //     beforeTest {
    //         performQuery (
    //             "DELETE * FROM books"
    //         )
    //     }
    // }

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:17").apply {
            withDatabaseName("booksdb")
            withUsername("username")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Autowired
    lateinit var bookDAO: BookDAO

    @Test
    fun `should save and retrieve books`() {
        val book = Book("Test Title", "Test Author")
        bookDAO.save(book)

        val books = bookDAO.findAll()
        books.size shouldBe 1
        books[0].title shouldBe "Test Title"
    }
}