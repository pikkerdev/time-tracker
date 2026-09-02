package e2e

import auth.Public
import klite.Config
import klite.Email
import klite.HttpExchange
import klite.annotations.GET
import klite.annotations.PathParam
import klite.oauth.initSession
import users.AuthRole
import users.User
import users.UserRepository

@Public
class E2eAuthRoutes(private val users: UserRepository) {
  @GET("/:role")
  fun login(@PathParam role: String, exchange: HttpExchange): User {
    check(Config.optional("E2E_AUTH") == "true")
    val authRole = AuthRole.valueOf(role.uppercase())
    val email = Email("e2e-${authRole.name.lowercase()}@example.test")
    val user = users.by(User::email to email)
      ?.copy(authRole = authRole)
      ?.also(users::save)
      ?: User("E2E", authRole.name, email, authRole).also(users::save)
    exchange.initSession(user)
    return user
  }
}
