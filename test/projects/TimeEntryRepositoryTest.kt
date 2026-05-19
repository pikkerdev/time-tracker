package projects

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.project
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.user
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

  @BeforeEach fun before() {
    userRepository.save(user)
    customerRepository.save(customer)
    projectRepository.save(project)
  }

  @Test fun `get time entry`() {
    repository.save(timeEntry)
    expect(repository.get(timeEntry.id)).toEqual(timeEntry)
  }

  @Test fun `list time entries`() {
    repository.save(timeEntry)
    expect(repository.listView()).toContainExactly(timeEntryView)
    expect(repository.listView(user.id)).toContainExactly(timeEntryView)
    expect(repository.listView(user.id, timeEntry.date)).toContainExactly(timeEntryView)
    expect(repository.listView(user.id, timeEntry.date.plusDays(3))).toBeEmpty()
  }
}
