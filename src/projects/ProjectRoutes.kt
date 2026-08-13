package projects

import auth.Access
import db.Id
import db.Status.ACTIVE
import db.Status.DELETED
import invoices.InvoiceRepository
import klite.Email
import klite.ForbiddenException
import klite.annotations.*
import klite.d
import klite.jdbc.eq
import projects.ProjectMember.Role
import projects.ProjectMember.Role.DEVELOPER
import timeentries.TimeEntryRepository
import users.AuthRole.*
import users.User
import users.UserRepository

class ProjectRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository,
  val timeEntryRepository: TimeEntryRepository,
  val userRepository: UserRepository,
  val invoiceRepository: InvoiceRepository
) {
  @GET("/:id") @Access(ADMIN, INTERNAL, EXTERNAL, CUSTOMER)
  fun get(@PathParam id: Id<Project>, @AttrParam user: User): ProjectView {
    if (user.authRole == ADMIN || projectMemberRepository.isMember(id, user.id)) {
      val timeStats = timeEntryRepository.statsForProject(id)
      val invoiceStats = invoiceRepository.statsForProject(id)
      val allMonths = timeStats.keys + invoiceStats.keys

      val combinedStats = allMonths.associateWith { month ->
        val time = timeStats[month]
        val invoice = invoiceStats[month]

        MonthlyStats(
          billedHours = (time?.billedHours ?: 0.d) + (invoice?.billedHours ?: 0.d),
          unbilledHours = (time?.unbilledHours ?: 0.d) + (invoice?.unbilledHours ?: 0.d),
          billedRevenue = (time?.billedRevenue ?: 0.d) + (invoice?.billedRevenue ?: 0.d),
          unbilledRevenue = (time?.unbilledRevenue ?: 0.d) + (invoice?.unbilledRevenue ?: 0.d)
        )
      }

      return ProjectView(
        project = projectRepository.get(id),
        stats = combinedStats
      )
    } else throw ForbiddenException()
  }

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

  @DELETE("/:id") @Access(ADMIN)
  fun delete(@PathParam id: Id<Project>) =
    projectRepository.setStatus(id, DELETED)

  @POST("/:id/members") @Access(ADMIN)
  fun saveMember(@PathParam id: Id<Project>, req: ProjectMemberRequest): ProjectMemberUser {
    val user = userRepository.by(User::email eq req.email)
      ?: User(
        firstName = req.firstName ?: "",
        lastName = req.lastName ?: "",
        email = req.email,
        authRole = EXTERNAL
      ).also { userRepository.save(it) }

    val role = when {
      user.isCustomer -> Role.CUSTOMER
      req.role == Role.CUSTOMER -> throw IllegalArgumentException("Cannot assign customer role to a non-customer user")
      else -> req.role
    }
    val projectMember = projectMemberRepository.find(id, user.id, active = false)
      ?.copy(role = role, status = ACTIVE)
      ?: ProjectMember(id, user.id, role = role)
    projectMemberRepository.save(projectMember)
    return ProjectMemberUser(projectMember, user)
  }

  @GET @Access(ADMIN, INTERNAL, EXTERNAL, CUSTOMER)
  fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false, @QueryParam includeDeleted: Boolean = false) =
    if (myProjects == true || user.authRole == EXTERNAL || user.authRole == CUSTOMER) projectRepository.forMember(user.id, includeDeleted)
    else if (!includeDeleted) projectRepository.listNotDeleted()
    else projectRepository.list()

  @GET("/:id/members") @Access(ADMIN, INTERNAL, EXTERNAL, CUSTOMER)
  fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)

  @DELETE("/member/:id") @Access(ADMIN) // TODO: memberS and projectId
  fun deleteMember(@PathParam id: Id<ProjectMember>) =
    projectMemberRepository.delete(id)
}

data class ProjectMemberRequest(val email: Email, val firstName: String? = null, val lastName: String? = null, val role: Role = DEVELOPER)
