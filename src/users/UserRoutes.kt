package users

import auth.Access
import klite.annotations.GET

@Access(AuthRole.ADMIN)
class UserRoutes(
  val userRepository: UserRepository
) {
  @GET
  fun list() = userRepository.list()

  @GET("/authroles")
  fun roles() = AuthRole.entries.associateBy({ it.name }, { it.name })
}
