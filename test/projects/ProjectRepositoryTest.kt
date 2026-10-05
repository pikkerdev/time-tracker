package projects

import ch.tutteli.atrium.api.fluent.en_GB.notToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import customers.CustomerRepository
import db.DBTest
import db.Status.ACTIVE
import db.Status.DELETED
import db.TestData.customer
import db.TestData.invoice
import db.TestData.project
import db.TestData.project2
import db.TestData.project3
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.user
import invoices.InvoiceRepository
import klite.d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import projects.ProjectMember.Role.CUSTOMER
import timeentries.TimeEntryRepository
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

  @Test fun `spent includes unbilled time and invoices`() {
    repository.save(project)
    TimeEntryRepository(db).apply { save(timeEntry); save(timeEntry2) }
    InvoiceRepository(db).save(invoice)
    expect(repository.get(project.id).spent).toEqual(915.d)
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

  @Test fun `set status`() {
    repository.save(project)
    repository.setStatus(project.id, DELETED)
    expect(repository.get(project.id).status).toEqual(DELETED)

    repository.setStatus(project.id, ACTIVE)
    expect(repository.get(project.id).status).toEqual(ACTIVE)
  }

  @Test fun `set statuses`() {
    repository.save(project)
    repository.save(project2)

    repository.setStatuses(customer.id, DELETED)
    expect(repository.get(project.id).status).toEqual(DELETED)
    expect(repository.get(project2.id).status).toEqual(DELETED)

    repository.setStatuses(customer.id, ACTIVE)
    expect(repository.get(project.id).status).toEqual(ACTIVE)
    expect(repository.get(project2.id).status).toEqual(ACTIVE)
  }
}
