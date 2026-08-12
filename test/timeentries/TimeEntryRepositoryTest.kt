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
import db.TestData.customerUser
import db.TestData.date
import db.TestData.invoice
import db.TestData.project
import db.TestData.project3
import db.TestData.projectMember
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.timeEntry3
import db.TestData.timeEntryView
import db.TestData.today
import db.TestData.twoDaysAgo
import db.TestData.user
import db.TestData.yesterday
import invoices.InvoiceRepository
import invoices.InvoiceRow
import klite.d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.MonthlyStats
import projects.ProjectMember
import projects.ProjectMember.Role.ARCHITECT
import projects.ProjectMemberRepository
import projects.ProjectRepository
import users.UserRepository

class TimeEntryRepositoryTest: DBTest() {
  val repository = TimeEntryRepository(db)
  val projectRepository = ProjectRepository(db)
  val userRepository = UserRepository(db)
  val customerRepository = CustomerRepository(db)
  val invoiceRepository = InvoiceRepository(db)
  val projectMemberRepository = ProjectMemberRepository(db)

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

  @Test fun listViewForCustomer() {
    userRepository.save(customerUser)
    projectMemberRepository.save(projectMember.copy(userId = customerUser.id, role = ProjectMember.Role.CUSTOMER))
    repository.save(timeEntry)
    repository.save(timeEntry3)
    expect(repository.listViewForCustomer(customerUser.id, from = date)).toContainExactly(timeEntryView)
    expect(repository.listViewForCustomer(customerUser.id, project.id, from = date, to = today)).toContainExactly(timeEntryView)
    expect(repository.listViewForCustomer(customerUser.id, from = today.plusDays(3))).toBeEmpty()
  }

  @Test fun userTimes() {
    val entry = timeEntry.copy(date = yesterday, hours = 3.d, id = Id())
    val entry2 = timeEntry.copy(date = yesterday, hours = 4.d, id = Id())
    val entry3 = timeEntry.copy(date = twoDaysAgo, hours = 2.d, id = Id())
    val entry4 = timeEntry3.copy(date = twoDaysAgo)

    repository.save(entry)
    repository.save(entry2)
    repository.save(entry3)
    repository.save(entry4)

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

  @Test fun `list view by ids`(){
    repository.save(timeEntry)
    expect(repository.listViewByIds(listOf(timeEntry.id))).toContainExactly(timeEntryView)
  }

  @Test fun `update invoice id`() {
    repository.save(timeEntry)
    repository.updateInvoiceId(listOf(timeEntry.id), invoice.id)
    expect(repository.get(timeEntry.id).invoiceId).toEqual(invoice.id)
  }

  @Test fun `initial rows`() {
    repository.save(timeEntry.copy(invoiceId = invoice.id))
    repository.save(timeEntry2.copy(invoiceId = invoice.id))
    repository.save(timeEntry3.copy(invoiceId = invoice.id, projectId = project.id, hours = 4.d, hourlyRate = 100.d, role = ARCHITECT))

    expect(repository.initialRows(listOf(timeEntry.id, timeEntry2.id, timeEntry3.id))).toContain(
      InvoiceRow("DEVELOPER", 660.d, 7.5.d, 88.d),
      InvoiceRow("DEVELOPER", 240.d, 4.d, 60.d),
      InvoiceRow("ARCHITECT", 400.d, 4.d, 100.d),
    )
  }

  @Test fun delete() {
    repository.save(timeEntry)
    repository.delete((timeEntry.id))
    expect(repository.list()).toBeEmpty()
  }

  @Test fun `update hourly rates`(){
    repository.save(timeEntry)
    repository.save(timeEntry2)
    repository.updateHourlyRates(listOf(timeEntry.id, timeEntry2.id), 10.d)
    expect(repository.get(timeEntry.id).hourlyRate).toEqual(10.d)
    expect(repository.get(timeEntry2.id).hourlyRate).toEqual(10.d)
  }

  @Test fun `stats for project`() {
    val entry = timeEntry.copy(date = yesterday, hours = 3.d, id = Id())
    val entry2 = timeEntry.copy(date = yesterday, hours = 4.d, id = Id(), invoiceId = invoice.id)
    val entry3 = timeEntry.copy(date = twoDaysAgo, hours = 2.d, id = Id())
    val entry4 = timeEntry3.copy(date = today, hours = 6.d, id = Id())
    val entry5 = timeEntry.copy(date = today.withYear(2024), hours = 2.d, id = Id())

    val projectStats1 = mapOf(today.withDayOfMonth(1) to MonthlyStats(0.d, 5.d, 0.d, 5.d.times(timeEntry.hourlyRate)),
      today.withYear(2024).withDayOfMonth(1) to MonthlyStats(0.d, 2.d, 0.d, 2.d.times(timeEntry.hourlyRate)))
    val projectStats2 = mapOf(today.withDayOfMonth(1) to MonthlyStats(0.d, 6.d, 0.d, 6.d.times(timeEntry3.hourlyRate)))

    invoiceRepository.save(invoice)
    repository.save(entry)
    repository.save(entry2)
    repository.save(entry3)
    repository.save(entry4)
    repository.save(entry5)

    expect(repository.statsForProject(project.id)).toEqual(projectStats1)
    expect(repository.statsForProject(project3.id)).toEqual(projectStats2)

  }
}
