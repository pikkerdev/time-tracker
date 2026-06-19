package projects

import ch.tutteli.atrium.api.fluent.en_GB.notToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.project
import db.TestData.project2
import db.TestData.project3
import db.TestData.user
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectMember.Role.CUSTOMER
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
    repository.save(project2)
    repository.save(project3)
    memberRepository.save(ProjectMember(project.id, user.id))
    memberRepository.save(ProjectMember(project2.id,user.id, CUSTOMER))
    expect(repository.forMember(user.id, false)).toContainExactly(project, project2)
    expect(repository.byCustomer(customer.id)).toContain(project)
    expect(repository.listNotDeleted()).toContain(project)
    expect(repository.listNotDeleted()).notToContain(project3)
    expect(repository.list()).toContain(project)
    expect(repository.list()).toContain(project3)
  }


  @Test fun delete() {
    repository.save(project)
    repository.save(project2)
    repository.delete(project.id)
    expect(repository.listNotDeleted()).notToContain(project)
  }
}
