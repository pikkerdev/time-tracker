package projects

import auth.Access
import db.Id
import klite.ForbiddenException
import klite.annotations.*
import projects.ProjectMember.Role
import projects.ProjectMember.Role.DEVELOPER
import projects.ProjectMember.Status.ACTIVE
import users.AuthRole.*
import users.User
import users.UserRepository

class ProjectRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository,
  val userRepository: UserRepository,
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

  @DELETE("/:id") @Access(ADMIN)
  fun delete(@PathParam id: Id<Project>) =
    projectRepository.delete(id)

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
  fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false, @QueryParam noCustomer: Boolean = false, @QueryParam includeDeleted: Boolean = false) =
    if (myProjects == true || user.authRole != ADMIN) projectRepository.forMember(user.id, noCustomer, includeDeleted)
    else if (!includeDeleted) projectRepository.listNotDeleted()
    else projectRepository.list()

  @GET("/:id/members") @Access(ADMIN, USER, EXTERNAL)
  fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)

  @DELETE("/member/:id") @Access(ADMIN) // TODO: memberS and projectId
  fun deleteMember(@PathParam id: Id<ProjectMember>) =
    projectMemberRepository.delete(id)
}

data class ProjectMemberRequest(val userId: Id<User>, val role: Role = DEVELOPER)
