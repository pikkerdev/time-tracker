package projects

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.admin
import db.TestData.project
import db.TestData.projectMember
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import project.ProjectMemberUser
import project.ProjectRoutes

class ProjectRoutesTest: BaseMocks() {
  val routes = create<ProjectRoutes>()

  @Test fun get() {
    expect(routes.get(project.id)).toEqual(project)
  }

  @Test fun create() {
    val newProject = routes.create(user, project)
    expect(newProject).toEqual(project)
    verify {
      projectRepository.save(newProject)
      projectMemberRepository.save(match {
        it.projectId == project.id && it.userId == user.id
      })
    }
  }

  @Test fun save() {
    val updatedProject = project.copy(name = "Updated name")
    val newProject = routes.save(updatedProject, project.id)
    expect(newProject).toEqual(updatedProject)
    verify { projectRepository.save(updatedProject) }
  }

  @Test fun `list for member`() {
    val projects = listOf(project)
    every { projectRepository.listForMember(user.id) } returns projects
    expect(routes.list(user)).toEqual(projects)
  }

  @Test fun `list for admin`() {
    val projects = listOf(project)
    expect(routes.list(admin)).toEqual(projects)
  }

  @Test fun members() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    val projectMembers = listOf(projectMemberUser)
    every { projectMemberRepository.list(project.id) } returns projectMembers
    expect(routes.members(project.id)).toEqual(projectMembers)
  }

  }
