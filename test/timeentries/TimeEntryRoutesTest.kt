package timeentries

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.admin
import db.TestData.customerUser
import db.TestData.date
import db.TestData.project
import db.TestData.projectMember
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.today
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import klite.d
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TimeEntryRoutesTest: BaseMocks() {
  val routes = create<TimeEntryRoutes>()

  @Test fun `add time entry`() {
    every { projectMemberRepository.find(project.id, user.id) } returns projectMember
    val rate = project.hourlyRates.getValue(projectMember.role)
    routes.saveTimeEntry(user, timeEntry)
    verify { timeEntryRepository.save(timeEntry.copy(userId = user.id, hourlyRate = rate)) }
  }

  @Test fun `edit time entry`() {
    every { projectMemberRepository.find(project.id, user.id) } returns projectMember
    val rate = project.hourlyRates.getValue(projectMember.role)
    val timeEntry =(timeEntry.copy( hours = 3.5.d))
    routes.editTimeEntry(user, timeEntry.id, timeEntry)
    verify { timeEntryRepository.save(timeEntry.copy(userId = user.id, hourlyRate = rate)) }
  }

  @Test fun `list time entries`() {
    val timeEntries = listOf(timeEntryView)
    every { timeEntryRepository.listView() } returns listOf(timeEntryView)
    every { timeEntryRepository.listView(user.id) } returns listOf(timeEntryView)
    every { timeEntryRepository.listView(user.id, to = today) } returns listOf(timeEntryView)
    every { timeEntryRepository.listView(user.id, project.id, date, today) } returns listOf(timeEntryView)
    every { timeEntryRepository.listView(projectId = project.id, from = date, to = today) } returns listOf(timeEntryView)

    expect(routes.timeEntries(user)).toContainExactly(timeEntryView)
    expect(routes.timeEntries(user, myTimeEntries = true)).toEqual(timeEntries)
    expect(routes.timeEntries(user, myTimeEntries = true, to = today)).toEqual(timeEntries)
    expect(routes.timeEntries(user, myTimeEntries = false, project.id, from = date, to = today)).toEqual(timeEntries)
    expect(routes.timeEntries(admin, myTimeEntries = false, project.id, from = date, to = today)).toEqual(timeEntries)

    every { timeEntryRepository.listViewForCustomer(customerUser.id) } returns listOf(timeEntryView)
    every { timeEntryRepository.listViewForCustomer(customerUser.id, to = today) } returns listOf(timeEntryView)
    every { timeEntryRepository.listViewForCustomer(customerUser.id, projectId = project.id, from = date, to = today) } returns listOf(timeEntryView)

    expect(routes.timeEntries(customerUser)).toContainExactly(timeEntryView)
    expect(routes.timeEntries(customerUser, to = today)).toEqual(timeEntries)
    expect(routes.timeEntries(customerUser, projectId = project.id, from = date, to = today)).toEqual(timeEntries)
  }

  @Test fun `user times`() {
    val userTimes = mapOf(LocalDate.now() to mapOf("#D7A262" to 7.d), LocalDate.now().minusDays(1) to mapOf("#D7A262" to 2.d))
    val from = LocalDate.now().minusDays(1)
    every { timeEntryRepository.userTimes(user.id, from) } returns userTimes
    expect(routes.userTimes(user, from)).toEqual(userTimes)

    val userTime = mapOf(LocalDate.now() to mapOf("#D7A262" to 7.d))
    val singleDay = LocalDate.now()
    every { timeEntryRepository.userTimes(user.id, singleDay) } returns userTime
    expect(routes.userTimes(user, singleDay)).toEqual(userTime)
  }

  @Test fun delete() {
    routes.delete(user, timeEntry.id)
    verify { timeEntryRepository.delete(timeEntry.id) }
  }

  @Test fun `update hourly rates`() {
    val req = UpdateHourlyRatesRequest(4.5.d, listOf(timeEntry.id))
    routes.updateHourlyRates(req)
    verify { timeEntryRepository.updateHourlyRates(listOf(timeEntry.id), 4.5.d) }

    every {timeEntryRepository.listViewByIds(listOf(timeEntry.id))} returns listOf(timeEntryView)
    expect(routes.updateHourlyRates(req)).toEqual(listOf(timeEntryView))
  }

}
