package users

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.Id
import db.TestData.user
import klite.BadRequestException
import org.junit.jupiter.api.Test

class UserRoutesTest: BaseMocks() {
  val routes = create<UserRoutes>()

  @Test fun list(){
    expect(routes.list()).toEqual(listOf(user))
  }
  @Test fun save(){
    val currentUser = user.copy(id = Id())
    expect (routes.save(user, user.id, currentUser)).toEqual(user)
    expect { (routes.save(user, user.id, user)) }.toThrow<BadRequestException>()
  }
}
