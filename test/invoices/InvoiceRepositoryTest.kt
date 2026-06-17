package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.invoice
import db.TestData.project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectRepository

class InvoiceRepositoryTest: DBTest() {
  val repository = InvoiceRepository(db)

  @BeforeEach fun setUp() {
    CustomerRepository(db).save(customer)
    ProjectRepository(db).save(project)
  }

  @Test fun `save & next id`() {
    expect(repository.nextId(invoice.date)).toEqual(invoice.id)

    repository.save(invoice)
    expect(repository.nextId(invoice.date)).toEqual(InvoiceId(invoice.id.value + 1))
  }
}
