package project

import auth.Access
import db.Id
import klite.Decimal
import klite.ForbiddenException
import klite.annotations.*
import project.ProjectMember.Role
import project.ProjectMember.Role.DEVELOPER
import project.ProjectMember.Status.ACTIVE
import users.AuthRole.*
import users.User
import users.UserRepository
import java.time.LocalDate

class ProjectRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository,
  val userRepository: UserRepository,
  val timeEntryRepository: TimeEntryRepository
) {
  @GET("/:id") @Access(ADMIN, USER, EXTERNAL)
  fun get(@PathParam id: Id<Project>, @AttrParam user: User) =
    if (user.authRole == ADMIN || projectMemberRepository.isMember(id, user.id)) projectRepository.get(id)
    else throw ForbiddenException()

  @POST @Access(ADMIN)
  fun create(@AttrParam user: User, project: Project): Project {
    projectRepository.save(project)
    projectMemberRepository.save(ProjectMember(project.id, user.id))
    return project
  }

  @POST("/:id") @Access(ADMIN)
  fun save(project: Project, @PathParam id: Id<Project>): Project {
    require(id == project.id) { "Wrong id" }
    projectRepository.save(project)
    return project
  }

  @POST("/:id/members") @Access(ADMIN)
  fun saveMember(@PathParam id: Id<Project>, member: ProjectMemberRequest): ProjectMemberUser {
    val projectMember = projectMemberRepository.find(id, member.userId, active = false)
      ?.copy(role = member.role, status = ACTIVE)
      ?: ProjectMember(id, member.userId, role = member.role)
    projectMemberRepository.save(projectMember)
    val user = userRepository.get(projectMember.userId)
    return ProjectMemberUser(projectMember, user)
  }

  @GET @Access(ADMIN, USER, EXTERNAL)
  fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false, @QueryParam noCustomer: Boolean = false) =
    if (myProjects == true || user.authRole != ADMIN) projectRepository.forMember(user.id, noCustomer)
    else projectRepository.list()

  @GET("/:id/members") @Access(ADMIN, USER, EXTERNAL)
  fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)

  @DELETE("/member/:id") @Access(ADMIN) // TODO: memberS and projectId
  fun deleteMember(@PathParam id: Id<ProjectMember>) =
    projectMemberRepository.delete(id)

  @POST("/timeentries") @Access(ADMIN, USER)
  fun saveTimeEntry(@AttrParam user: User, timeEntry: TimeEntry): TimeEntry {
    val project = projectRepository.get(timeEntry.projectId)
    val member = projectMemberRepository.find(timeEntry.projectId, user.id) ?: throw NoSuchElementException("member")
    val rate = project.hourlyRates[member.role] ?: throw NoSuchElementException("hourlyRates")
    val newTimeEntry = timeEntry.copy(userId = user.id, hourlyRate = rate)
    timeEntryRepository.save(newTimeEntry)
    return newTimeEntry
  }

  @POST("/timeentries/:id") @Access(ADMIN, USER)
  fun editTimeEntry( @PathParam id: Id<TimeEntry>, timeEntry: TimeEntry): TimeEntry {
    require(id == timeEntry.id) { "Wrong id" }
    timeEntryRepository.save(timeEntry)
    return timeEntry
  }

  @GET("/timeentries") @Access(ADMIN, USER)
  fun timeEntries(@AttrParam user: User, @QueryParam myTimeEntries: Boolean? = false, @QueryParam projectId: Id<Project>? = null, @QueryParam from: LocalDate? = null, @QueryParam to: LocalDate? = null) =
    if (myTimeEntries == true || user.authRole != ADMIN) timeEntryRepository.listView(user.id, projectId, from, to)
    else timeEntryRepository.listView(projectId = projectId, from = from, to = to)

  @GET("/timeentries/user") @Access(ADMIN, USER)
  fun userTimes(@AttrParam user: User, @QueryParam from: LocalDate, @QueryParam until: LocalDate = LocalDate.now()): Map<LocalDate, Decimal> =
    timeEntryRepository.userTimes(user.id, from, until)
}

data class ProjectMemberRequest(val userId: Id<User>, val role: Role = DEVELOPER)
