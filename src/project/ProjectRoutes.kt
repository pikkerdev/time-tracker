package project

import auth.Access
import db.Id
import klite.annotations.AttrParam
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import klite.annotations.QueryParam
import users.AuthRole.ADMIN
import users.User

@Access(ADMIN)
class ProjectRoutes(
  val projectRepository: ProjectRepository,
  val projectMemberRepository: ProjectMemberRepository)
{

  @GET("/:id")
  fun get(@PathParam id: Id<Project>) = projectRepository.get(id)

  @POST fun create(@AttrParam user: User, project: Project) : Project {
    projectRepository.save(project)
    projectMemberRepository.save(ProjectMember(project.id, user.id))
    return project
  }

  @POST("/:id")fun save(project: Project, @PathParam id: Id<Project>) :Project {
    require(id == project.id) { "Wrong id" }
    projectRepository.save(project)
    return project
  }

  @POST("/:id/members") fun save(@PathParam id: Id<Project>, userId: Id<User>) {
    println(userId.toString())
    projectMemberRepository.save(ProjectMember(id, userId))
  }

  @GET fun list(@AttrParam user: User, @QueryParam myProjects: Boolean? = false) =
    if (myProjects == true) projectRepository.listForMember(user.id)
    else if (user.authRole == ADMIN) projectRepository.list()
    else projectRepository.listForMember(user.id)

  @GET("/:id/members") fun members(@PathParam id: Id<Project>): List<ProjectMemberUser> =
    projectMemberRepository.list(id)
}
