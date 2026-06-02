package projects

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.Id
import db.TestData.customer
import db.TestData.date
import db.TestData.project
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.today
import db.TestData.twoDaysAgo
import db.TestData.user
import db.TestData.yesterday
import db.TestData.invoice
import invoice.InvoiceRepository
import klite.Decimal
import klite.d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import project.ProjectRepository
import project.TimeEntryRepository
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
    invoiceRepository.save(invoice)
  }

  @Test fun `get time entry`() {
    repository.save(timeEntry)
    expect(repository.get(timeEntry.id)).toEqual(timeEntry)
  }

  @Test fun listView() {
    repository.save(timeEntry)
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
    expect(repository.userTimes(user.id, from = yesterday, until = yesterday)).toEqual(mapOf(yesterday to 7.d))
    expect(repository.userTimes(user.id, until = twoDaysAgo)).toEqual(mapOf(twoDaysAgo to 2.d))

    val userTimes = mapOf(yesterday to Decimal(7.0), twoDaysAgo to 2.d)
    expect(repository.userTimes(user.id, from = twoDaysAgo)).toEqual(userTimes)
  }
}
