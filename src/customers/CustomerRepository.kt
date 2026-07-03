package customers

import db.CrudRepository
import db.Id
import klite.jdbc.update
import db.Status
import db.Status.DELETED
import klite.jdbc.eq
import klite.jdbc.neq
import klite.jdbc.select
import javax.sql.DataSource

class CustomerRepository(db: DataSource): CrudRepository<Customer>(db, "customers") {

  private val notDeleted = "$table.status" neq DELETED
  private val deleted = "$table.status" eq DELETED

  fun setStatus(id: Id<Customer>, status: Status) {
    db.update(table, mapOf(Customer::status to status), Customer::id to id)
  }

  fun list(isDeleted: Boolean = false): List<Customer> =
    db.select("$table ",
      if (isDeleted) deleted else notDeleted) { mapper() }
}
