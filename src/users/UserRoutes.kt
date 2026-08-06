package users

import auth.Access
import db.Id
import klite.BadRequestException
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.PathParam
import klite.annotations.AttrParam
import klite.Config
import users.AuthRole.ADMIN

@Access(ADMIN)
class UserRoutes(
  val userRepository: UserRepository,
){

  private val initialAdmins = Config["INITIAL_ADMINS"].split(",")

  @GET
  fun list() = userRepository.list()

  @POST("/:id")
  fun save(userToSave: User, @PathParam id: Id<User>, @AttrParam user: User): User {
    val email = userToSave.email.toString()
    require(id == userToSave.id) { "Wrong id" }
    if (user.id == id && user.authRole !== userToSave.authRole) throw BadRequestException("errors.cannotModifyYourRole")
    else if (email in initialAdmins && user.authRole !== userToSave.authRole) throw BadRequestException("errors.cannotModifyInitialAdminRole")
    else userRepository.save(userToSave)
    return userToSave
  }
}
