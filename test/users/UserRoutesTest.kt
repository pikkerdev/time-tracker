package users

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.Id
import db.TestData.user
import klite.BadRequestException
import klite.Email
import org.junit.jupiter.api.Test
import users.AuthRole.INTERNAL

class UserRoutesTest: BaseMocks() {
  val routes = create<UserRoutes>()

  @Test fun list(){
    expect(routes.list()).toEqual(listOf(user))
  }
  @Test fun save(){
    val currentUser = user.copy(id = Id())
    val userToSave = user.copy(authRole = INTERNAL)
    expect (routes.save(userToSave, user.id, currentUser)).toEqual(userToSave)
    expect { (routes.save(userToSave, user.id, user)) }.toThrow<BadRequestException>()
  }

  @Test fun `modifying initial admin forbidden`() {
    val currentUser = user.copy(email = Email("rasmus@pikker.dev"))
    val userToSave = user.copy(authRole = INTERNAL)
    expect { (routes.save(userToSave, user.id, currentUser)) }.toThrow<BadRequestException>()
  }

}
