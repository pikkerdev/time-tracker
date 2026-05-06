package project

import auth.Access
import db.Id
import klite.ForbiddenException
import klite.annotations.AttrParam
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import klite.annotations.QueryParam
import users.AuthRole.USER
import users.AuthRole.EXTERNAL
import users.AuthRole.ADMIN
import users.User
import java.math.BigDecimal

class ProjectRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository)
{

  @GET("/:id") @Access(ADMIN, USER, EXTERNAL)
  fun get(@PathParam id: Id<Project>, @AttrParam user: User): ProjectWithCustomer? =
    if (user.authRole == ADMIN || projectMemberRepository.isMember(id, user.id)) projectRepository.getWithCustomer(id)
    else throw ForbiddenException()

  @POST @Access(ADMIN)
  fun create(@AttrParam user: User, project: Project) : Project {
    projectRepository.save(project)
    projectMemberRepository.save(ProjectMember(project.id, user.id))
    return project
  }

  @POST("/:id") @Access(ADMIN)
    fun save(project: Project, @PathParam id: Id<Project>) :Project {
    require(id == project.id) { "Wrong id" }
    projectRepository.save(project)
    return project
  }

  @POST("/:id/members") @Access(ADMIN)
    fun save(@PathParam id: Id<Project>, userId: Id<User>) {
    println(userId.toString())
    projectMemberRepository.save(ProjectMember(id, userId))
  }

  @GET @Access(ADMIN, USER, EXTERNAL)
  fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false) =
    if (myProjects == true || user.authRole != ADMIN) projectRepository.listForMemberWithCustomer(user.id)
    else projectRepository.listWithCustomers()

  @GET("/:id/members") @Access(ADMIN, USER, EXTERNAL)
  fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)
}

data class ProjectDto(
  val id: Id<Project>,
  val customerName: String,
  val name: String,
  val description: String? = null,
  val currency: String = "EUR",
  val hourlyRate: BigDecimal,
  val storyTrackerId: Int? = null,
)
