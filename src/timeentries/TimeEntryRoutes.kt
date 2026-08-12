package timeentries

import auth.Access
import db.Id
import klite.Decimal
import klite.ForbiddenException
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
  internal fun checkModifyAccess(user: User, id: Id<TimeEntry>){
    if (timeEntryRepository.get(id).userId == user.id || user.isAdmin) {
      return
    }
    else throw ForbiddenException()
  }

  @GET @Access(ADMIN, INTERNAL, EXTERNAL, CUSTOMER)
  fun timeEntries(@AttrParam user: User, @QueryParam myTimeEntries: Boolean? = false, @QueryParam projectId: Id<Project>? = null, @QueryParam from: LocalDate? = null, @QueryParam to: LocalDate? = null) =
    if (myTimeEntries == true || user.isInternal || user.isExternal) timeEntryRepository.listView(user.id, projectId, from, to)
    else if (user.isCustomer) timeEntryRepository.listViewForCustomer(user.id, projectId, from, to)
    else timeEntryRepository.listView(projectId = projectId, from = from, to = to)

  @POST @Access(ADMIN, INTERNAL, EXTERNAL)
  fun saveTimeEntry(@AttrParam user: User, timeEntry: TimeEntry): TimeEntry {
    val project = projectRepository.get(timeEntry.projectId)
    val member = projectMemberRepository.find(timeEntry.projectId, user.id) ?: throw NoSuchElementException("member")
    val rate = project.hourlyRates[member.role] ?: throw NoSuchElementException("hourlyRates")
    val newTimeEntry = timeEntry.copy(userId = user.id, hourlyRate = rate, role = member.role)
    require(((timeEntryRepository.userTimes(user.id, timeEntry.date, timeEntry.date).get(timeEntry.date)?.values?.fold(0.d) { sum, element ->
      sum.plus(element)
    }?: 0.d) + timeEntry.hours) <= 24.d){"Too many hours"}
    timeEntryRepository.save(newTimeEntry)
    return newTimeEntry
  }

  @POST("/:id") @Access(ADMIN, INTERNAL, EXTERNAL)
  fun editTimeEntry(@AttrParam user: User, @PathParam id: Id<TimeEntry>, timeEntry: TimeEntry): TimeEntry {
    checkModifyAccess(user, id)
    require(timeEntry.invoiceId == null) {"Can not edit time entry that is in invoice"}
    require(id == timeEntry.id) { "Wrong id" }
    val oldTimeEntry = timeEntryRepository.get(id)
    val user = userRepository.get(timeEntry.userId)
    require(((timeEntryRepository.userTimes(user.id, timeEntry.date, timeEntry.date).get(timeEntry.date)?.values?.fold(0.d) { sum, element ->
      sum.plus(element)
    } ?: 0.d) + timeEntry.hours - oldTimeEntry.hours) <= 24.d) {"Too many hours"}
    timeEntryRepository.save(timeEntry)
    return timeEntry
  }

  @GET("/user") @Access(ADMIN, INTERNAL, EXTERNAL)
  fun userTimes(@AttrParam user: User, @QueryParam from: LocalDate, @QueryParam until: LocalDate = LocalDate.now()): Map<LocalDate, Map<String, Decimal>> =
    timeEntryRepository.userTimes(user.id, from, until)

  @DELETE("/:id") @Access(ADMIN, INTERNAL, EXTERNAL) fun delete(@AttrParam user: User, @PathParam id: Id<TimeEntry>) {
    checkModifyAccess(user, id)
    require(timeEntryRepository.get(id).invoiceId == null) {"Can not delete time entry that is in invoice"}
    timeEntryRepository.delete(id)
  }

  @POST("/hourlyRates") @Access(ADMIN)
  fun updateHourlyRates(req: UpdateHourlyRatesRequest): List<TimeEntryView> {
    val timeEntries = timeEntryRepository.listViewByIds(req.timeEntryIds)
    require(timeEntries.all { it.entry.invoiceId == null }) {"Can not edit time entry that is in invoice"}
    timeEntryRepository.updateHourlyRates(req.timeEntryIds, req.rate)
    return timeEntryRepository.listViewByIds(req.timeEntryIds)
  }
}

data class UpdateHourlyRatesRequest(
  val rate: Decimal,
  val timeEntryIds: List<Id<TimeEntry>>
)
