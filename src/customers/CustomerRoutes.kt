package customers

import auth.Access
import db.Id
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import projects.ProjectRepository
import users.AuthRole.ADMIN
import users.AuthRole.INTERNAL

@Access(ADMIN, INTERNAL)
class CustomerRoutes(
  private val repository: CustomerRepository,
  private val projectRepository: ProjectRepository
) {

  @POST fun create(customer: Customer): Customer {
    repository.save(customer)
    return customer
  }

  @GET fun list() = repository.list()

  @GET("/:id/projects") fun projects(@PathParam id: Id<Customer>) =
    projectRepository.byCustomer(id)
}
