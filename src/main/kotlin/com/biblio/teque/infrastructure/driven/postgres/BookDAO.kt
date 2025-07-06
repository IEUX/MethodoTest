package com.biblio.teque.infrastructure.driven.postgres

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BooksRepository {

    /** Insert ou update (sur le titre) */
    override fun save(book: Book) {
        val sql = """
            INSERT INTO books(title, author, is_rented)
            VALUES (:title, :author, :is_rented)
            ON CONFLICT (title)
            DO UPDATE SET author = EXCLUDED.author,
                        is_rented = EXCLUDED.is_rented
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            MapSqlParameterSource()
                .addValue("title",    book.title)
                .addValue("author",   book.author)
                .addValue("is_rented", book.isRented)
        )
    }

    /** Tous les livres triés par titre */
    override fun findAll(): List<Book> =
        jdbcTemplate.query(
            "SELECT title, author, is_rented FROM books ORDER BY title",
            rowMapper                       // ← même nom partout
        )

    /** Recherche par titre (utile pour rentBook) */
    override fun findByTitle(title: String): Book? =
        jdbcTemplate.query(
            "SELECT title, author, is_rented FROM books WHERE title = :title LIMIT 1",
            MapSqlParameterSource("title", title),
            rowMapper
        ).firstOrNull()

    /** Conversion ResultSet → Book */
    private val rowMapper = RowMapper { rs, _ ->
        Book(
            title    = rs.getString("title"),
            author   = rs.getString("author"),
            isRented = rs.getBoolean("is_rented")
        )
    }
}
