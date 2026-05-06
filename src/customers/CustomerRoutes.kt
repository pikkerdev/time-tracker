package customers

import auth.Access
import db.Id
import klite.annotations.AttrParam
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import project.ProjectRepository
import users.AuthRole.ADMIN
import users.User

@Access(ADMIN)
class CustomerRoutes(
  private val repository: CustomerRepository,
  private val projectRepository: ProjectRepository
) {

  @POST fun create(customer: Customer): Customer {
    repository.save(customer)
    return customer
  }

  @GET fun list() = repository.list()

  @GET("/:id/projects") fun projects(@PathParam id: Id<Customer>, @AttrParam user: User) =
    projectRepository.dtoListByCustomerAndMember(id, user.id)
}
