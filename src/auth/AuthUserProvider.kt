package auth

import klite.Config
import klite.HttpExchange
import klite.oauth.OAuthTokenResponse
import klite.oauth.OAuthUser
import klite.oauth.OAuthUserProvider
import klite.oauth.UserProfile
import users.AuthRole
import users.User
import users.UserRepository

class AuthUserProvider(
  private val userRepository: UserRepository
): OAuthUserProvider {
  private val admins = Config["INITIAL_ADMINS"].split(",")
  private val users = Config["OWN_DOMAIN"]

  override fun provide(profile: UserProfile, tokenResponse: OAuthTokenResponse, exchange: HttpExchange): OAuthUser {
    val email = profile.email.toString()


    var user = userRepository.by(User::email to profile.email)
    if (user == null) {
      val authRole = when {
        admins.contains(email) -> AuthRole.ADMIN
        email.endsWith("@$users") -> AuthRole.USER
        else -> AuthRole.EXTERNAL
      }
      user = User(profile.firstName, profile.lastName, profile.email, authRole, profile.avatarUrl)
      userRepository.save(user)
    } else {
      user = user.copy(authRole = user.authRole)
    }
    userRepository.save(user)
    return user
  }
  }


