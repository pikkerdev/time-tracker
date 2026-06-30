package timeentries

import auth.Access
import db.Id
import klite.Decimal
import klite.annotations.*
import klite.d
import projects.Project
import projects.ProjectMemberRepository
import projects.ProjectRepository
import users.AuthRole.*
import users.User
import users.UserRepository
import java.time.LocalDate

class TimeEntryRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository,
  val timeEntryRepository: TimeEntryRepository,
  val userRepository: UserRepository
) {
  @GET() @Access(ADMIN, INTERNAL, EXTERNAL, CUSTOMER)
  fun timeEntries(@AttrParam user: User, @QueryParam myTimeEntries: Boolean? = false, @QueryParam projectId: Id<Project>? = null, @QueryParam from: LocalDate? = null, @QueryParam to: LocalDate? = null) =
    if (myTimeEntries == true || user.isInternal || user.isExternal) timeEntryRepository.listView(user.id, projectId, from, to)
    else timeEntryRepository.listView(projectId = projectId, from = from, to = to)

  @POST() @Access(ADMIN, INTERNAL, EXTERNAL)
  fun saveTimeEntry(@AttrParam user: User, timeEntry: TimeEntry): TimeEntry {
    val project = projectRepository.get(timeEntry.projectId)
    val member = projectMemberRepository.find(timeEntry.projectId, user.id) ?: throw NoSuchElementException("member")
    val rate = project.hourlyRates[member.role] ?: throw NoSuchElementException("hourlyRates")
    val newTimeEntry = timeEntry.copy(userId = user.id, hourlyRate = rate, role = member.role)
    require(((userTimes(user, timeEntry.date, timeEntry.date).get(timeEntry.date)?: 0.d) + timeEntry.hours) <= 24.d){"Too many hours"}
    timeEntryRepository.save(newTimeEntry)
    return newTimeEntry
  }

  @POST("/:id") @Access(ADMIN, INTERNAL, EXTERNAL)
  fun editTimeEntry(@PathParam id: Id<TimeEntry>, timeEntry: TimeEntry): TimeEntry {
    require(timeEntry.invoiceId == null) {"Can not edit time entry that is in invoice"}
    require(id == timeEntry.id) { "Wrong id" }
    val oldTimeEntry = timeEntryRepository.get(timeEntry.id)
    val user = userRepository.get(timeEntry.userId)
    require(((userTimes(user, timeEntry.date, timeEntry.date).get(timeEntry.date)?: 0.d) + timeEntry.hours - oldTimeEntry.hours) <= 24.d) {"Too many hours"}
    timeEntryRepository.save(timeEntry)
    return timeEntry
  }

  @GET("/user") @Access(ADMIN, INTERNAL, EXTERNAL)
  fun userTimes(@AttrParam user: User, @QueryParam from: LocalDate, @QueryParam until: LocalDate = LocalDate.now()): Map<LocalDate, Decimal> =
    timeEntryRepository.userTimes(user.id, from, until)
}
