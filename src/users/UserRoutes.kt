package users

import auth.Access
import klite.annotations.GET
import klite.annotations.POST

@Access(AuthRole.ADMIN)
class UserRoutes(
  val userRepository: UserRepository
) {
  @GET
  fun list() = userRepository.list()
}

