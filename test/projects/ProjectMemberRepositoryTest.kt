package projects

import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import org.junit.jupiter.api.Test
import project.ProjectMemberRepository
import project.ProjectRepository
import db.TestData.project
import db.TestData.projectMember
import db.TestData.user
import org.junit.jupiter.api.Assertions.assertEquals
import project.ProjectMemberUser
import users.UserRepository

class ProjectMemberRepositoryTest: DBTest() {
  val repository = ProjectMemberRepository(db)
  val projectRepository = ProjectRepository(db)
  val userRepository = UserRepository(db)
  val customerRepository = CustomerRepository(db)

  @Test fun list() {
    customerRepository.save(customer)
    projectRepository.save(project)
    userRepository.save(user)
    repository.save(projectMember)
    val projectMemberUser = ProjectMemberUser(projectMember, user)
    expect(repository.list(project.id)).toContain(projectMemberUser)
  }

  @Test fun isMember() {
    customerRepository.save(customer)
    projectRepository.save(project)
    userRepository.save(user)
    repository.save(projectMember)
    assertEquals(true, repository.isMember(project.id, user.id))  }

  @Test fun delete() {
    customerRepository.save(customer)
    projectRepository.save(project)
    userRepository.save(user)
    repository.save(projectMember)
    repository.delete(projectMember.id)
    expect(repository.list(project.id)).toBeEmpty()
  }

}
