package users

import auth.Access
import db.Id
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam

@Access(AuthRole.ADMIN)
class UserRoutes(
  val userRepository: UserRepository
) {
  @GET
  fun list() = userRepository.list()

  @POST("/:id")
  fun save(user: User, @PathParam id: Id<User>): User {
    require(id == user.id) { "Wrong id" }
    userRepository.save(user)
    return user
  }
}

