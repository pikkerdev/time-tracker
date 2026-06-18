package timeentries

import auth.Access
import db.Id
import klite.Decimal
import klite.annotations.*
import projects.Project
import projects.ProjectMemberRepository
import projects.ProjectRepository
import users.AuthRole.ADMIN
import users.AuthRole.INTERNAL
import users.User
import java.time.LocalDate

class TimeEntryRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository,
  val timeEntryRepository: TimeEntryRepository
) {
  @GET() @Access(ADMIN, INTERNAL)
  fun timeEntries(@AttrParam user: User, @QueryParam myTimeEntries: Boolean? = false, @QueryParam projectId: Id<Project>? = null, @QueryParam from: LocalDate? = null, @QueryParam to: LocalDate? = null) =
    if (myTimeEntries == true || user.authRole != ADMIN) timeEntryRepository.listView(user.id, projectId, from, to)
    else timeEntryRepository.listView(projectId = projectId, from = from, to = to)

  @POST() @Access(ADMIN, INTERNAL)
  fun saveTimeEntry(@AttrParam user: User, timeEntry: TimeEntry): TimeEntry {
    val project = projectRepository.get(timeEntry.projectId)
    val member = projectMemberRepository.find(timeEntry.projectId, user.id) ?: throw NoSuchElementException("member")
    val rate = project.hourlyRates[member.role] ?: throw NoSuchElementException("hourlyRates")
    val newTimeEntry = timeEntry.copy(userId = user.id, hourlyRate = rate)
    timeEntryRepository.save(newTimeEntry)
    return newTimeEntry
  }

  @POST("/:id") @Access(ADMIN, INTERNAL)
  fun editTimeEntry(@PathParam id: Id<TimeEntry>, timeEntry: TimeEntry): TimeEntry {
    require(timeEntry.invoiceId == null) {"Can not edit time entry that is in invoice"}
    require(id == timeEntry.id) { "Wrong id" }
    timeEntryRepository.save(timeEntry)
    return timeEntry
  }

  @GET("/user") @Access(ADMIN, INTERNAL)
  fun userTimes(@AttrParam user: User, @QueryParam from: LocalDate, @QueryParam until: LocalDate = LocalDate.now()): Map<LocalDate, Decimal> =
    timeEntryRepository.userTimes(user.id, from, until)
}
