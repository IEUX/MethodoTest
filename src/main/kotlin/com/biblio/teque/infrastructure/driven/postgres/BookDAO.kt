package com.biblio.teque.infrastructure.driven.postgres

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository

@Repository
class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BooksRepository {

    override fun save(book: Book) {
        val sql = "INSERT INTO books(title, author) VALUES(:title, :author)"
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
        jdbcTemplate.update(sql, params)
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT title, author FROM books ORDER BY title"
        return jdbcTemplate.query(sql, map)
    }

    private val map = RowMapper { rs, _ ->
        Book(
            title = rs.getString("title"),
            author = rs.getString("author")
        )
    }
}