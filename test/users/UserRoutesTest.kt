package users

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.user
import org.junit.jupiter.api.Test

class UserRoutesTest: BaseMocks() {
  val routes = create<UserRoutes>()

  @Test fun list(){
    expect(routes.list()).toEqual(listOf(user))
  }

}
