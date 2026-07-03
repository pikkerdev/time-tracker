package customers

import auth.Access
import db.Id
import db.Status
import db.Status.ACTIVE
import db.Status.DELETED
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import klite.annotations.QueryParam
import projects.ProjectRepository
import users.AuthRole.ADMIN
import users.AuthRole.INTERNAL

@Access(ADMIN, INTERNAL)
class CustomerRoutes(
  private val repository: CustomerRepository,
  private val projectRepository: ProjectRepository
) {

  @POST @Access(ADMIN) fun create(customer: Customer): Customer {
    repository.save(customer)
    return customer
  }

  @GET fun list(@QueryParam isDeleted: Boolean = false) =
    repository.list(isDeleted)

  @GET("/:id/projects") fun projects(@PathParam id: Id<Customer>) =
    projectRepository.byCustomer(id)

  @POST("/:id") @Access(ADMIN)
  fun setStatus(@PathParam id: Id<Customer>, status: Status) {
    repository.setStatus(id, status)
    when (status) {
      ACTIVE, DELETED -> projectRepository.setStatuses(id, status)
    }
  }
}
