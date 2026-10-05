package users

import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.DBTest
import db.TestData.admin
import db.TestData.customerUser
import db.TestData.user
import org.junit.jupiter.api.Test

class UserRepositoryTest: DBTest() {
  val repository = UserRepository(db)

  @Test fun `save & load`() {
    repository.save(user)
    expect(repository.get(user.id)).toEqual(user)
    expect(repository.list()).toContain(user)
  }

  @Test fun `list is ordered by name`() {
    listOf(user, admin, customerUser).forEach { repository.save(it) }
    expect(repository.list()).toEqual(listOf(admin, customerUser, user))
  }
}
