package projects

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.Id
import db.TestData.customer
import db.TestData.project
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.user
import klite.Decimal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import project.ProjectRepository
import project.TimeEntryRepository
import users.UserRepository
import java.time.LocalDate

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

  @Test fun `user times`() {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val twoDaysAgo = today.minusDays(2)

    val entry = timeEntry.copy(date = yesterday, hours = Decimal(3.0), id = Id())
    val entry2 = timeEntry.copy(date = yesterday, hours = Decimal(4.0), id = Id())
    val entry3 = timeEntry.copy(date = twoDaysAgo, hours = Decimal(2.0), id = Id())

    repository.save(entry)
    repository.save(entry2)
    repository.save(entry3)

    expect(repository.userTimes(user.id, singleDay = today)).toBeEmpty()
    expect(repository.userTimes(user.id, singleDay = yesterday)).toEqual(mapOf(yesterday to Decimal(7.0)))
    expect(repository.userTimes(user.id, singleDay = twoDaysAgo)).toEqual(mapOf(twoDaysAgo to Decimal(2.0)))

    val userTimes: Map<LocalDate, Decimal> = mapOf(
      yesterday to Decimal(7.0),
      twoDaysAgo to Decimal(2.0)
    )

    expect(repository.userTimes(user.id, twoDaysAgo, today)).toEqual(userTimes)
  }
}
