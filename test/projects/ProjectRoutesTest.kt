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
import db.TestData.projectMemberUser
import db.TestData.timeEntry
import db.TestData.timeEntryView
import db.TestData.user
import io.mockk.every
import io.mockk.verify
import klite.Decimal
import klite.ForbiddenException
import org.junit.jupiter.api.Test
import project.ProjectMemberUser
import project.ProjectRoutes
import java.time.LocalDate
import project.ProjectMember.Status.ACTIVE

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
    every { projectRepository.forMember(user.id) } returns listOf(project)
    expect(routes.list(user)).toContainExactly(project)

    every { projectRepository.list() } returns listOf(project)
    expect(routes.list(admin)).toContainExactly(project)

    every { projectRepository.forMember(admin.id) } returns listOf(project)
    expect(routes.list(admin, myProjects = true)).toContainExactly(project)
  }

  @Test fun members() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    val projectMembers = listOf(projectMemberUser)
    every { projectMemberRepository.list(project.id) } returns projectMembers
    expect(routes.members(project.id)).toEqual(projectMembers)
  }

    @Test fun `create member`() {
      every { projectMemberRepository.find(project.id, user.id, active = false) } returns null
      val result = routes.create(project.id, user.id)
      expect(result.user).toEqual(user)
      expect(result.member.projectId).toEqual(project.id)
      expect(result.member.userId).toEqual(user.id)
      verify { projectMemberRepository.save(match {it.projectId == project.id && it.userId == user.id}) }
    }

   @Test fun `save member`() {
    every { projectMemberRepository.find(project.id, user.id, active = false) } returns projectMember
    expect (routes.save(project.id, projectMember.id, projectMember)).toEqual(projectMemberUser)
    verify {projectMemberRepository.save(projectMember.copy(status = ACTIVE))}
   }

   @Test fun `delete member`() {
      routes.deleteMember(projectMember.id)
      verify { projectMemberRepository.delete(projectMember.id) }
    }

    @Test fun `add time entry`() {
      every { projectMemberRepository.find(project.id, user.id) } returns projectMember
      val rate = project.hourlyRates.getValue(projectMember.role)
      routes.saveTimeEntry(user, timeEntry)
      verify { timeEntryRepository.save(timeEntry.copy(userId = user.id, hourlyRate = rate)) }
    }

    @Test fun `list time entries`() {
      val timeEntries = listOf(timeEntryView)
      every { timeEntryRepository.listView() } returns listOf(timeEntryView)
      every { timeEntryRepository.listView(user.id) } returns listOf(timeEntryView)
      every { timeEntryRepository.listView(user.id, date) } returns listOf(timeEntryView)
      expect(routes.timeEntries(user, myTimeEntries = true, date)).toEqual(timeEntries)
    }

    @Test fun `user times`() {
      val userTimes = mapOf(LocalDate.now() to Decimal(7.0), LocalDate.now().minusDays(1) to Decimal(2.0))
      val from = LocalDate.now().minusDays(1)
      every { timeEntryRepository.userTimes(user.id, from) } returns userTimes
      expect(routes.userTimes(user, from)).toEqual(userTimes)

      val userTime = mapOf(LocalDate.now() to Decimal(7.0))
      val singleDay = LocalDate.now()
      every { timeEntryRepository.userTimes(user.id, singleDay) } returns userTime
      expect(routes.userTimes(user, singleDay)).toEqual(userTime)
    }
  }

