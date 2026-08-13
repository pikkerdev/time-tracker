package projects

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.Status.ACTIVE
import db.Status.DELETED
import db.TestData.admin
import db.TestData.project
import db.TestData.projectMember
import db.TestData.projectMemberUser
import db.TestData.projectStats
import db.TestData.projectView
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import klite.ForbiddenException
import org.junit.jupiter.api.Test
import users.User

class ProjectRoutesTest: BaseMocks() {
  val routes = create<ProjectRoutes>()

  @Test fun get() {
    every { timeEntryRepository.statsForProject(project.id) } returns projectStats
    every { invoiceRepository.statsForProject(project.id) } returns projectStats

    expect(routes.get(project.id, admin)).toEqual(projectView)
    every { projectMemberRepository.isMember(project.id, user.id) } returns true
    expect(routes.get(project.id, user)).toEqual(projectView)

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
    verify { projectRepository.setStatus(project.id, DELETED) }
  }

  @Test fun members() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    val projectMembers = listOf(projectMemberUser)
    every { projectMemberRepository.list(project.id) } returns projectMembers
    expect(routes.members(project.id)).toEqual(projectMembers)
  }

    @Test fun `save member creates new`() {
      every { projectMemberRepository.find(project.id, user.id, active = false) } returns null
      every { userRepository.by(User::email to user.email) } returns user
      val result = routes.saveMember(project.id, ProjectMemberRequest(user.email))
      expect(result.user).toEqual(user)
      expect(result.member.projectId).toEqual(project.id)
      expect(result.member.userId).toEqual(user.id)
      verify { projectMemberRepository.save(match { it.projectId == project.id && it.userId == user.id }) }
    }

   @Test fun `save member updates existing`() {
    every { projectMemberRepository.find(project.id, user.id, active = false) } returns projectMember
     every { userRepository.by(User::email to user.email) } returns user
     expect(routes.saveMember(project.id, ProjectMemberRequest(user.email, role = projectMember.role))).toEqual(projectMemberUser)
    verify { projectMemberRepository.save(projectMember.copy(role = projectMember.role, status = ACTIVE)) }
   }

  @Test
  fun `save member creates new user`() {
    every { userRepository.by(User::email to user.email) } returns null
    every { projectMemberRepository.find(any(), any(), any()) } returns null

    val result = routes.saveMember(project.id, ProjectMemberRequest(user.email, user.firstName, user.lastName, role = projectMember.role))
    verify { userRepository.save(any()) }
    verify { projectMemberRepository.save(any()) }
    expect(result).toEqual(ProjectMemberUser(projectMember.copy(id = result.member.id, userId = result.user.id, createdAt = result.member.createdAt), user.copy(id = result.user.id, createdAt = result.user.createdAt)))
  }

   @Test fun `delete member`() {
      routes.deleteMember(projectMember.id)
      verify { projectMemberRepository.delete(projectMember.id) }
    }
  }

