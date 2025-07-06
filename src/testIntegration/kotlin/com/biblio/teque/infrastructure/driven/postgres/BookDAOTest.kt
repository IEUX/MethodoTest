package com.biblio.teque.infrastructure.driven.postgres

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import com.biblio.teque.domain.model.Book
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

@Testcontainers
@SpringBootTest
class BookDAOTest {

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
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    lateinit var bookDAO: BookDAO

    @BeforeEach
    fun resetDatabase() {
        jdbcTemplate.update("DROP TABLE IF EXISTS books", MapSqlParameterSource())
        jdbcTemplate.update(
            """
            CREATE TABLE books (
                title TEXT PRIMARY KEY,
                author TEXT NOT NULL,
                is_rented BOOLEAN NOT NULL
            )
            """.trimIndent(),
            MapSqlParameterSource()
        )
    }

    @Test
    fun `save should insert a book`() {
        val book = Book("Dune", "Frank Herbert", isRented = false)
        bookDAO.save(book)

        val found = bookDAO.findByTitle("Dune")
        found shouldBe book
    }

    @Test
    fun `save should update if book already exists`() {
        val book = Book("Dune", "Frank Herbert", isRented = false)
        bookDAO.save(book)

        val updated = Book("Dune", "F. Herbert", isRented = true)
        bookDAO.save(updated)

        val found = bookDAO.findByTitle("Dune")
        found shouldBe updated
    }

    @Test
    fun `findAll should return all books sorted`() {
        bookDAO.save(Book("Z", "Author Z", false))
        bookDAO.save(Book("A", "Author A", false))

        val result = bookDAO.findAll()
        result shouldContainExactly listOf(
            Book("A", "Author A", false),
            Book("Z", "Author Z", false)
        )
    }

    @Test
    fun `findByTitle should return null if not found`() {
        bookDAO.findByTitle("Nonexistent").shouldBeNull()
    }
}
