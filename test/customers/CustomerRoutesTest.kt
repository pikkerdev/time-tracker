package customers

import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.customer
import db.TestData.projectDto
import db.TestData.user
import io.mockk.every
import io.mockk.verify
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
    every { customerRepository.list() } returns customers
    expect(routes.list()).toEqual(customers)
  }

  @Test fun projects() {
    every { projectRepository.dtoListByCustomer(customer.id) } returns listOf(projectDto)
    expect(routes.projects(customer.id)).toContain(projectDto)
  }

}
