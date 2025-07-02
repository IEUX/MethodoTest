package com.biblio.teque.infrastructure.application

import com.biblio.teque.infrastructure.application.InMemoryBooksRepository
import com.biblio.teque.domain.usecase.ManageBooksUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun manageBooksUseCase(repository: InMemoryBooksRepository): ManageBooksUseCase {
        return ManageBooksUseCase(repository)
    }
}
