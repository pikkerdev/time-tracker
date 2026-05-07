package users

import auth.Access
import db.Id
import klite.BadRequestException
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import klite.annotations.AttrParam

@Access(AuthRole.ADMIN)
class UserRoutes(
  val userRepository: UserRepository
)  {
  @GET
  fun list() = userRepository.list()

  @POST("/:id")
  fun save(userToSave: User, @PathParam id: Id<User>, @AttrParam user: User): User {
    require(id == userToSave.id) { "Wrong id" }
    if (user.id == id) {
      throw BadRequestException("errors.cannotModifyYourRole")
    }
    userRepository.save(userToSave)
    return userToSave
  }
}
