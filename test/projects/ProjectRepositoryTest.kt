package projects

import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.TestData.customer
import db.TestData.project
import db.TestData.projectWithCustomer
import db.TestData.user
import io.mockk.every
import org.junit.jupiter.api.Test
import project.ProjectMember
import project.ProjectMemberRepository
import project.ProjectRepository
import users.UserRepository

class ProjectRepositoryTest: DBTest() {
  val repository = ProjectRepository(db)
  val memberRepository = ProjectMemberRepository(db)

  @Test fun `get with customer`() {
    CustomerRepository(db).save(customer)
    UserRepository(db).save(user)
    repository.save(project)
    expect(repository.getWithCustomer(project.id)).toEqual(projectWithCustomer)
  }

  @Test fun `get lists`() {
    CustomerRepository(db).save(customer)
    UserRepository(db).save(user)
    repository.save(project)
    memberRepository.save(ProjectMember(project.id, user.id))
    expect(repository.listForMemberWithCustomer(user.id)).toContain(projectWithCustomer)
    expect(repository.listForCustomerAndMember(customer.id, user.id)).toContain(projectWithCustomer)
    expect(repository.listWithCustomers()).toContain(projectWithCustomer)
  }
}
