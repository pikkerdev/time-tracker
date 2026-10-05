package users

import db.CrudRepository
import klite.jdbc.exec
import javax.sql.DataSource

class UserRepository(db: DataSource): CrudRepository<User>(db, "users") {
  override val orderAsc = "order by $table.firstName, $table.lastName"
  override val defaultOrder get() = orderAsc

  fun setAppUser(user: User) { db.exec("call set_app_user(?)", user.id) }
}
