package projects

import ch.tutteli.atrium.api.fluent.en_GB.notToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.project
import db.TestData.project2
import db.TestData.projectMember
import db.TestData.projectMember2
import db.TestData.user
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectMember.Status.DELETED
import users.UserRepository


class ProjectMemberRepositoryTest: DBTest() {
  val repository = ProjectMemberRepository(db)
  val projectRepository = ProjectRepository(db)
  val userRepository = UserRepository(db)
  val customerRepository = CustomerRepository(db)

  @BeforeEach fun before() {
    customerRepository.save(customer)
    projectRepository.save(project)
    userRepository.save(user)
    repository.save(projectMember)
  }

  @Test fun list() {
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    expect(repository.list(project.id)).toContain(projectMemberUser)
    val deletedMember = projectMember.copy(status = DELETED)
    expect(repository.list(project.id)).notToContain(ProjectMemberUser(deletedMember, user))
  }

  @Test fun isMember() {
    expect(repository.isMember(project.id, user.id)).toEqual(true)
    repository.delete(projectMember.id)
    expect(repository.isMember(project.id, user.id)).toEqual(false)
  }

  @Test fun delete() {
    repository.delete(projectMember.id)
    expect(repository.list()).notToContain(projectMember)
  }

  @Test fun find() {
    expect(repository.find(project.id, user.id, active = true)).toEqual(projectMember)
    repository.delete(projectMember.id)
    val deletedProjectMember = projectMember.copy(status = DELETED)
    expect(repository.find(project.id, user.id, active = false)).toEqual(deletedProjectMember)
  }

  @Test fun findUserProjectMembers() {
    projectRepository.save(project2)
    repository.save(projectMember2)

    expect(repository.findUserProjectMembers(user.id)).toEqual(listOf(projectMember, projectMember2))

    repository.delete(projectMember.id)
    expect(repository.findUserProjectMembers(user.id)).toEqual(listOf(projectMember2))
    expect(repository.findUserProjectMembers(user.id, active = false)).toContain(projectMember2, projectMember.copy(status = DELETED))
  }
}
