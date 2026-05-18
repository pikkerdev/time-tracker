package projects

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.admin
import db.TestData.date
import db.TestData.project
import db.TestData.projectMember
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import klite.ForbiddenException
import org.junit.jupiter.api.Test
import project.ProjectMemberUser
import project.ProjectRoutes

class ProjectRoutesTest: BaseMocks() {
  val routes = create<ProjectRoutes>()

  @Test fun get() {
    expect(routes.get(project.id, admin)).toEqual(project)
    every{ projectMemberRepository.isMember(project.id, user.id) } returns true
    expect(routes.get(project.id, user)).toEqual(project)
  }

  @Test fun `get access forbidden`(){
    every{ projectMemberRepository.isMember(project.id, user.id) } returns false
    expect { routes.get(project.id, user) }.toThrow<ForbiddenException>()
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
    every { projectRepository.forMember(user.id) } returns listOf(project)
    expect(routes.list(user)).toContainExactly(project)
  }

  @Test fun `list for admin`() {
    every { projectRepository.list() } returns listOf(project)
    expect(routes.list(admin)).toContainExactly(project)
  }

  @Test fun `list own projects for admin`() {
    every { projectRepository.forMember(admin.id) } returns listOf(project)
    expect(routes.list(admin, myProjects = true)).toContainExactly(project)
  }

  @Test fun members() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    val projectMembers = listOf(projectMemberUser)
    every { projectMemberRepository.list(project.id) } returns projectMembers
    expect(routes.members(project.id)).toEqual(projectMembers)
  }

  @Test fun `delete member`() {
    routes.deleteMember(projectMember.id)
    verify { projectMemberRepository.delete(projectMember.id) }
  }

  @Test fun `add time entry`() {
    every { projectMemberRepository.find(project.id, user.id) } returns projectMember
    val rate = project.hourlyRates.getValue(projectMember.role)
    routes.addTimeEntry(user, timeEntry)
    verify { timeEntryRepository.save(timeEntry.copy(userId = user.id, hourlyRate = rate)) }
  }

  @Test fun `list time entries`() {
    val timeEntries = listOf(timeEntryView)
    every { timeEntryRepository.listView() } returns listOf(timeEntryView)
    every { timeEntryRepository.listViewByUser(user.id) } returns listOf(timeEntryView)
    every { timeEntryRepository.listViewByUserAndDate(user.id, date) }  returns listOf(timeEntryView)
    expect (routes.listTimeEntryByDateAndUser(user, date)).toEqual(timeEntries)
    expect (routes.listTimeEntry(user)).toEqual(timeEntries)
  }
  }
