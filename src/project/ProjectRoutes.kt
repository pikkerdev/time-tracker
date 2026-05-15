package project

import auth.Access
import db.Id
import klite.ForbiddenException
import klite.NotFoundException
import klite.annotations.*
import users.AuthRole.*
import users.User
import users.UserRepository

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
  fun create(@PathParam id: Id<Project>, userId: Id<User>): ProjectMemberUser {
    val projectMember = ProjectMember(id, userId)
    projectMemberRepository.save(projectMember)
    val user = userRepository.get(projectMember.userId)
    return ProjectMemberUser(projectMember, user)
  }

  @POST("/:id/members/:memberId") @Access(ADMIN)
  fun save(@PathParam id: Id<Project>, @PathParam memberId: Id<ProjectMember>, projectMember: ProjectMember): ProjectMemberUser {
    require(memberId == projectMember.id ) { "Wrong id" }
    projectMemberRepository.save(projectMember)
    val user = userRepository.get(projectMember.userId)
    return ProjectMemberUser(projectMember, user)
  }

  @GET @Access(ADMIN, USER, EXTERNAL)
  fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false) =
    if (myProjects == true || user.authRole != ADMIN) projectRepository.forMember(user.id)
    else projectRepository.list()

  @GET("/:id/members") @Access(ADMIN, USER, EXTERNAL)
  fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)

  @DELETE("/member/:id") @Access(ADMIN)
  fun deleteMember(@PathParam id: Id<ProjectMember>) =
    projectMemberRepository.delete(id)

  @POST("/timeentry") @Access(ADMIN, USER)
  fun addTimeEntry (@AttrParam user: User, timeEntry: TimeEntry) : TimeEntry {
    val project = projectRepository.get(timeEntry.projectId)
    val member = projectMemberRepository.find(timeEntry.projectId, user.id) ?: throw NotFoundException("general.notFound")
    val rate = project.hourlyRates[member.role] ?: throw NotFoundException("general.notFound")
    val newTimeEntry = timeEntry.copy(userId = user.id, hourlyRate = rate)
    timeEntryRepository.save(newTimeEntry)
    return newTimeEntry
  }

  @GET("/timeentries") @Access(ADMIN, USER)
  fun listTimeEntry(@AttrParam user: User, @QueryParam myTimeEntries: Boolean? = false) =
    if (myTimeEntries == true || user.authRole != ADMIN) timeEntryRepository.forUser(user.id)
    else timeEntryRepository.list()
}
