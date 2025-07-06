package com.biblio.teque.domain.usecase

import com.biblio.teque.domain.model.Book
import com.biblio.teque.domain.port.BooksRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*

class ManageBooksUseCaseTest : DescribeSpec({

    val repository = mockk<BooksRepository>(relaxed = true)
    val useCase = ManageBooksUseCase(repository)

    describe("addBook") {

        it("should throw when title is blank") {
            shouldThrow<IllegalArgumentException> {
                Book("   ", "Author")
            }
        }

        it("should throw when author is blank") {
            shouldThrow<IllegalArgumentException> {
                Book("Title", "")
            }
        }

        it("should create a not rented book") {
            val book = Book("Title", "Author")
            book.isRented shouldBe false
        }
    }

    describe("listBooks") {

        it("should return all books sorted by title") {
            val unsorted = listOf(
                Book("Z Book", "Author X"),
                Book("A Book", "Author A"),
                Book("M Book", "Author M")
            )
            every { repository.findAll() } returns unsorted

            val result = useCase.listBooks()

            result shouldContainExactly listOf(
                Book("A Book", "Author A"),
                Book("M Book", "Author M"),
                Book("Z Book", "Author X")
            )
        }
    }

     describe("rentBook") {

        it("should mark book as rented when available") {
            val book = Book("Dune", "Frank Herbert")
            every { repository.findByTitle("Dune") } returns book

            useCase.rentBook("Dune")

            book.isRented shouldBe true
            verify { repository.save(book) }
        }

        it("should throw when book already rented") {
            val book = Book("Dune", "Frank Herbert", isRented = true)
            every { repository.findByTitle("Dune") } returns book

            shouldThrow<IllegalStateException> { useCase.rentBook("Dune") }
        }

        it("should throw when book not found") {
            every { repository.findByTitle("Unknown") } returns null

            shouldThrow<IllegalArgumentException> { useCase.rentBook("Unknown") }
        }
    }
    
})