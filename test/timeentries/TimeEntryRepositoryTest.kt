package timeentries

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.Id
import db.TestData.customer
import db.TestData.date
import db.TestData.invoice
import db.TestData.project
import db.TestData.project3
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.timeEntry3
import db.TestData.timeEntryView
import db.TestData.today
import db.TestData.twoDaysAgo
import db.TestData.user
import db.TestData.yesterday
import invoices.InvoiceRepository
import invoices.RoleHoursEntry
import klite.Decimal
import klite.d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectMember.Role.ARCHITECT
import projects.ProjectMember.Role.DEVELOPER
import projects.ProjectRepository
import users.UserRepository

class TimeEntryRepositoryTest: DBTest() {
  val repository = TimeEntryRepository(db)
  val projectRepository = ProjectRepository(db)
  val userRepository = UserRepository(db)
  val customerRepository = CustomerRepository(db)
  val invoiceRepository = InvoiceRepository(db)

  @BeforeEach fun before() {
    userRepository.save(user)
    customerRepository.save(customer)
    projectRepository.save(project)
    projectRepository.save(project3)
    invoiceRepository.save(invoice)
  }

  @Test fun `get time entry`() {
    repository.save(timeEntry)
    expect(repository.get(timeEntry.id)).toEqual(timeEntry)
  }

  @Test fun listView() {
    repository.save(timeEntry)
    repository.save(timeEntry3)
    expect(repository.listView(from = date)).toContainExactly(timeEntryView)
    expect(repository.listView(user.id, from = date)).toContainExactly(timeEntryView)
    expect(repository.listView(user.id, project.id, from = date, to = today)).toContainExactly(timeEntryView)
    expect(repository.listView(user.id, from = today.plusDays(3))).toBeEmpty()
  }

  @Test fun userTimes() {
    val entry = timeEntry.copy(date = yesterday, hours = 3.d, id = Id())
    val entry2 = timeEntry.copy(date = yesterday, hours = 4.d, id = Id())
    val entry3 = timeEntry.copy(date = twoDaysAgo, hours = 2.d, id = Id())

    repository.save(entry)
    repository.save(entry2)
    repository.save(entry3)

    expect(repository.userTimes(user.id, from = today, until = today)).toBeEmpty()
    expect(repository.userTimes(user.id, from = yesterday, until = yesterday)).toEqual(mapOf(yesterday to mapOf("#D7A262" to 7.d)))
    expect(repository.userTimes(user.id, until = twoDaysAgo)).toEqual(mapOf(twoDaysAgo to mapOf("#D7A262" to 2.d)))

    val userTimes = mapOf(yesterday to mapOf("#D7A262" to 7.d), twoDaysAgo to mapOf("#D7A262" to 2.d),)
    expect(repository.userTimes(user.id, from = twoDaysAgo)).toEqual(userTimes)
  }

  @Test fun `list by ids`() {
    repository.save(timeEntry)
    expect(repository.listByIds(listOf(timeEntry.id))).toContainExactly(timeEntry)
  }

  @Test fun `update invoice id`() {
    repository.save(timeEntry)
    repository.updateInvoiceId(listOf(timeEntry.id), invoice.id)
    expect(repository.get(timeEntry.id).invoiceId).toEqual(invoice.id)
  }

  @Test fun `sum hours by role for invoices`() {
    repository.save(timeEntry.copy(invoiceId = invoice.id))
    repository.save(timeEntry2.copy(invoiceId = invoice.id))
    repository.save(timeEntry2.copy(id = Id(), invoiceId = invoice.id, hourlyRate = 100.d, role = ARCHITECT))

    expect(repository.sumHoursByRoleForInvoice(invoice.id)).toContain(
      RoleHoursEntry(DEVELOPER, 7.5.d, 88.d),
      RoleHoursEntry(DEVELOPER, 4.d, 60.d),
      RoleHoursEntry(ARCHITECT, 4.d, 100.d),
    )
  }
}
