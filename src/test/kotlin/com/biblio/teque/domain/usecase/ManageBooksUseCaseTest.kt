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

        // it("should save a valid book") {
        //     val title = "Clean Code"
        //     val author = "Robert C. Martin"

        //     val book = useCase.addBook(title, author)

        //     book shouldBe Book(title, author)
        //     verify { repository.save(book) }
        // }

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
})