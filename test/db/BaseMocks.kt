package db

import customers.CustomerRepository
import db.TestData.invoice
import db.TestData.project
import db.TestData.timeEntry
import db.TestData.user
import invoices.InvoiceRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import klite.Config
import klite.DependencyInjectingRegistry
import klite.HttpExchange
import klite.StatusCode.Companion.Found
import klite.StatusCodeException
import projects.ProjectMemberRepository
import projects.ProjectRepository
import timeentries.TimeEntryRepository
import users.UserRepository
import java.net.URI

abstract class BaseMocks {
  companion object {
    init {
      Config.useEnvFile()
    }

    val registry = DependencyInjectingRegistry()
    val exchange = mockk<HttpExchange>(relaxed = true)

    val userRepository = mock<UserRepository>(relaxed = true)
    val customerRepository = mock<CustomerRepository>(relaxed = true)
    val projectRepository = mock<ProjectRepository>(relaxed = true)
    val projectMemberRepository = mock<ProjectMemberRepository>(relaxed = true)
    val timeEntryRepository = mock<TimeEntryRepository>(relaxed = true)
    val invoiceRepository = mock<InvoiceRepository>(relaxed = true)

    inline fun <reified T: Any> create() = registry.create(T::class)

    inline fun <reified T: Any> mock(relaxed: Boolean = false, crossinline block: T.() -> Unit = {}) =
      mockk(relaxed = relaxed, relaxUnitFun = true, block = block).also { registry.register(T::class, it) }
  }

  init {
    clearAllMocks()
    exchange.apply {
      every { redirect(any<String>(), any()) } throws StatusCodeException(Found)
      every { redirect(any<URI>(), any()) } throws StatusCodeException(Found)
      every { fullUrl(any()) } answers { URI("https://host" + firstArg<String>()) }
    }

    userRepository.apply {
      every { get(user.id) } returns user
      every { save(any()) } returns 1
      every { get(TestData.admin.id) } returns TestData.admin
      every { list() } returns listOf(user)
    }

    projectRepository.apply {
      every { get(project.id) } returns project
      every { list()} returns listOf(project)
    }

    timeEntryRepository.apply {
      every {get(timeEntry.id)} returns timeEntry
    }

    invoiceRepository.apply {
      every {nextId(invoice.date)} returns invoice.id
      every {get(invoice.id)} returns invoice
    }

  }
}
