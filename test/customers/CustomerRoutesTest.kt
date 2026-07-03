package customers

import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.Status.ACTIVE
import db.Status.DELETED
import db.TestData.customer
import db.TestData.project
import io.mockk.every
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class ProjectRoutesTest: BaseMocks() {
  val routes = create<CustomerRoutes>()

  @Test fun create() {
    val newCustomer = routes.create(customer)
    expect(newCustomer).toEqual(customer)
    verify { customerRepository.save(newCustomer) }
  }

  @Test fun list() {
    val customers = listOf(customer)
    every { customerRepository.list(false) } returns customers
    expect(routes.list(false)).toEqual(customers)

    every { customerRepository.list(true) } returns customers
    expect(routes.list(true)).toEqual(customers)
  }

  @Test fun projects() {
    every { projectRepository.byCustomer(customer.id) } returns listOf(project)
    expect(routes.projects(customer.id)).toContain(project)
  }

  @Test fun setStatus() {
    routes.setStatus(customer.id, ACTIVE)
    routes.setStatus(customer.id, DELETED)

    verifySequence {
      customerRepository.setStatus(customer.id, ACTIVE)
      customerRepository.setStatus(customer.id, DELETED)
    }
  }
}
