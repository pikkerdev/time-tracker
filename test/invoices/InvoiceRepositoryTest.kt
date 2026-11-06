package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.fixedPriceInvoice
import db.TestData.invoice
import db.TestData.invoiceRow
import db.TestData.project
import db.TestData.project3
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.user
import invoices.Invoice.Status.*
import klite.d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.MonthlyStats
import projects.ProjectRepository
import timeentries.TimeEntryRepository
import users.UserRepository
import java.time.LocalDate

class InvoiceRepositoryTest: DBTest() {
  val repository = InvoiceRepository(db)

  @BeforeEach fun setUp() {
    UserRepository(db).save(user)
    CustomerRepository(db).save(customer)
    ProjectRepository(db).save(project)
  }


  @Test fun `save & load & delete`() {
    repository.save(invoice)

    expect(repository.list()).toContain(invoice)
    expect(repository.delete(invoice.id)).toEqual(true)

    expect(repository.list()).toBeEmpty()
  }

  @Test fun `save and load fixed price invoice`() {
    repository.save(fixedPriceInvoice)

    expect(repository.get(fixedPriceInvoice.id)).toEqual(fixedPriceInvoice)
  }

  @Test fun `next id`() {
    expect(repository.nextId(invoice.date)).toEqual(invoice.id)

    repository.save(invoice)
    expect(repository.nextId(invoice.date)).toEqual(InvoiceId(invoice.id.value + 1))
  }

  @Test fun `get invoice with IDs`() {
    repository.save(invoice)

    expect(repository.getWithIds(invoice.id).customerId).toEqual(customer.id)
    expect(repository.getWithIds(invoice.id).creatorId).toEqual(user.id)
  }

  @Test fun `list view`() {
    repository.save(invoice)

    val viewList = repository.listView(null)
    expect(viewList.size).toEqual(1)

    val view = viewList.first()
    expect(view.invoice).toEqual(invoice)
    expect(view.creatorName).toEqual(user.name)
    expect(view.customerName).toEqual(customer.name)
    expect(view.projectName).toEqual(project.name)
  }

  @Test fun `exception when deleting not existing invoice`() {
    expect{repository.delete(invoice.id)}.toThrow<NoSuchElementException>()
  }

  @Test fun `set status`() {
    repository.save(invoice)
    expect(repository.setStatus(invoice.id, SENT)).toEqual(true)
    repository.setStatus(invoice.id, SENT)
    expect(repository.get(invoice.id).status).toEqual(SENT)
    repository.setStatus(invoice.id, PAID)
    expect(repository.get(invoice.id).status).toEqual(PAID)
    repository.setStatus(invoice.id, CREATED)
    expect(repository.get(invoice.id).status).toEqual(CREATED)
  }

  @Test fun `set paid amount`() {
    repository.save(invoice)
    expect(repository.setPaidAmount(invoice.id, 5.d)).toEqual(true)
    expect(repository.get(invoice.id)).toEqual(invoice.copy(paidAmount = 5.d))
  }

  @Test fun `set payment`() {
    repository.save(invoice)
    expect(repository.setPayment(invoice.id, 5.d, SENT)).toEqual(true)
    expect(repository.get(invoice.id)).toEqual(invoice.copy(paidAmount = 5.d, status = SENT))
  }

  @Test fun `stats for project`() {
    val invoice1 = invoice.copy(id = InvoiceId(2026060101))
    val invoice2 = invoice.copy(id = InvoiceId(2026060102))
    val invoice3 = invoice.copy(id = InvoiceId(2026060103), revenueMonth = LocalDate.of(2024, 5, 1))
    val invoice4 = invoice.copy(id = InvoiceId(2026060104), projectId = project3.id)

    val projectStats1 = mapOf(LocalDate.of(2026, 5, 1) to MonthlyStats(6.d, 0.d, 30.d, 0.d, 0.d),
      LocalDate.of(2024, 5, 1) to MonthlyStats(3.d, 0.d, 15.d, 0.d, 0.d))
    val projectStats2 = mapOf(LocalDate.of(2026, 5, 1) to MonthlyStats(3.d, 0.d, 15.d, 0.d, 0.d))

    ProjectRepository(db).save(project3)
    repository.save(invoice1)
    repository.save(invoice2)
    repository.save(invoice3)
    repository.save(invoice4)

    expect(repository.statsForProject(project.id)).toEqual(projectStats1)
    expect(repository.statsForProject(project3.id)).toEqual(projectStats2)

  }

  @Test fun `stats for fixed price project`() {
    val fixedPriceInvoice = fixedPriceInvoice.copy(id = InvoiceId(2026060105), revenueMonth = LocalDate.of(2026, 5, 1))
    val timeEntryRepository = TimeEntryRepository(db)

    repository.save(fixedPriceInvoice)
    timeEntryRepository.save(timeEntry.copy(invoiceId = fixedPriceInvoice.id, hours = 4.d))
    timeEntryRepository.save(timeEntry2.copy(invoiceId = fixedPriceInvoice.id, hours = 6.d))

    expect(repository.statsForProject(project.id)).toEqual(mapOf(
      LocalDate.of(2026, 5, 1) to MonthlyStats(10.d, 0.d, 1000.d, 0.d, 0.d)
    ))
  }

  @Test fun `stats include zero amount invoice rows`() {
    val zeroRow = invoiceRow.copy(amount = 0.d, rate = 0.d)
    repository.save(invoice.copy(id = InvoiceId(2026060106), rows = listOf(zeroRow)))

    expect(repository.statsForProject(project.id)).toEqual(mapOf(
      LocalDate.of(2026, 5, 1) to MonthlyStats(3.d, 0.d, 0.d, 0.d, 0.d)
    ))
  }
}
