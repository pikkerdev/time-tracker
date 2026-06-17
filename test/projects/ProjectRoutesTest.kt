package projects

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.admin
import db.TestData.project
import db.TestData.projectMember
import db.TestData.projectMemberUser
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import klite.ForbiddenException
import org.junit.jupiter.api.Test
import projects.ProjectMember.Status.ACTIVE

class ProjectRoutesTest: BaseMocks() {
  val routes = create<ProjectRoutes>()

  @Test fun get() {
    expect(routes.get(project.id, admin)).toEqual(project)
    every { projectMemberRepository.isMember(project.id, user.id) } returns true
    expect(routes.get(project.id, user)).toEqual(project)

    every { projectMemberRepository.isMember(project.id, user.id) } returns false
    expect { routes.get(project.id, user) }.toThrow<ForbiddenException>()
  }

  @Test fun `create and save`() {
    val newProject = routes.create(user, project)
    expect(newProject).toEqual(project)
    verify {
      projectRepository.save(newProject)
      projectMemberRepository.save(match { it.projectId == project.id && it.userId == user.id })
    }

    val project = (project.copy(name = "Updated name"))
    val updatedProject = routes.save(project, project.id)
    expect(updatedProject).toEqual(project)
    verify { projectRepository.save(updatedProject) }
  }

  @Test fun `list projects`() {
    every { projectRepository.forMember(user.id, false) } returns listOf(project)
    expect(routes.list(user)).toContainExactly(project)
    every { projectRepository.forMember(user.id, true) } returns listOf(project)
    expect(routes.list(user)).toContainExactly(project)

    every { projectRepository.forMember(admin.id, false) } returns listOf(project)
    expect(routes.list(admin, myProjects = true)).toContainExactly(project)

    every { projectRepository.listNotDeleted() } returns listOf(project)
    expect(routes.list(admin)).toContainExactly(project)

    every { projectRepository.list() } returns listOf(project)
    expect(routes.list(admin, includeDeleted = true)).toContainExactly(project)

  }

  @Test fun delete() {
    routes.delete(project.id)
    verify { projectRepository.delete(project.id) }
  }

  @Test fun members() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    val projectMembers = listOf(projectMemberUser)
    every { projectMemberRepository.list(project.id) } returns projectMembers
    expect(routes.members(project.id)).toEqual(projectMembers)
  }

    @Test fun `save member creates new`() {
      every { projectMemberRepository.find(project.id, user.id, active = false) } returns null
      val result = routes.saveMember(project.id, ProjectMemberRequest(user.id))
      expect(result.user).toEqual(user)
      expect(result.member.projectId).toEqual(project.id)
      expect(result.member.userId).toEqual(user.id)
      verify { projectMemberRepository.save(match { it.projectId == project.id && it.userId == user.id }) }
    }

   @Test fun `save member updates existing`() {
    every { projectMemberRepository.find(project.id, user.id, active = false) } returns projectMember
    expect(routes.saveMember(project.id, ProjectMemberRequest(user.id, projectMember.role))).toEqual(projectMemberUser)
    verify { projectMemberRepository.save(projectMember.copy(role = projectMember.role, status = ACTIVE)) }
   }

   @Test fun `delete member`() {
      routes.deleteMember(projectMember.id)
      verify { projectMemberRepository.delete(projectMember.id) }
    }
  }

