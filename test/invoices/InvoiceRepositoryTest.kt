package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.Status.ACTIVE
import db.Status.DELETED
import db.TestData.customer
import db.TestData.invoice
import db.TestData.project
import db.TestData.user
import invoices.Invoice.Status.CREATED
import invoices.Invoice.Status.PAID
import invoices.Invoice.Status.SENT
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectRepository
import users.UserRepository

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

  @Test fun `next id`() {
    expect(repository.nextId(invoice.date)).toEqual(invoice.id)

    repository.save(invoice)
    expect(repository.nextId(invoice.date)).toEqual(InvoiceId(invoice.id.value + 1))
  }

  @Test fun `get invoice with customer ID`() {
    repository.save(invoice)

    expect(repository.getWithCustomerId(invoice.id).customerId).toEqual(customer.id)
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
}
