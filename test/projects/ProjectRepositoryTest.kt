package projects

import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.project
import db.TestData.user
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import project.ProjectMember
import project.ProjectMemberRepository
import project.ProjectRepository
import users.UserRepository

class ProjectRepositoryTest: DBTest() {
  val repository = ProjectRepository(db)
  val memberRepository = ProjectMemberRepository(db)

  @BeforeEach fun before() {
    CustomerRepository(db).save(customer)
    UserRepository(db).save(user)
  }

  @Test fun `get with customer`() {
    repository.save(project)
    expect(repository.get(project.id)).toEqual(project)
  }

  @Test fun `get lists`() {
    repository.save(project)
    memberRepository.save(ProjectMember(project.id, user.id))
    expect(repository.forMember(user.id)).toContain(project)
    expect(repository.byCustomer(customer.id)).toContain(project)
    expect(repository.list()).toContain(project)
  }
}
