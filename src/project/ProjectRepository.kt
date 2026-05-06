package project

import customers.Customer
import db.CrudRepository
import db.Id
import klite.jdbc.select
import users.User
import javax.sql.DataSource
import klite.jdbc.create
import kotlin.collections.listOf

class ProjectRepository (db: DataSource): CrudRepository<Project>(db, "projects") {

  fun getWithCustomer(id: Id<Project>): ProjectWithCustomer? =
    db.select("$table p join customers c on p.customerId = c.id", listOf("p.id" to id.value)) {
      ProjectWithCustomer(create(), create("c."))
    }.firstOrNull()

  fun listWithCustomers(): List<ProjectWithCustomer> =
    db.select("$table p join customers c on p.customerId = c.id") {
      ProjectWithCustomer(create(), create("c."))
    }

  fun listForMemberWithCustomer(userId: Id<User>): List<ProjectWithCustomer> =
    db.select("$table p join customers c on p.customerId = c.id join project_members pm on p.id = pm.projectId", listOf("pm.userId" to userId.value)) {
      ProjectWithCustomer(create(), create("c."))
    }

  fun listForCustomerAndMember(customerId: Id<Customer>, userId: Id<User>): List<ProjectWithCustomer> =
    db.select("$table p join customers c on p.customerId = c.id join project_members m on p.id = m.projectId",
      listOf("p.customerId" to customerId.value, "m.userId" to userId.value)) {
      ProjectWithCustomer(create(), create("c."))
    }

}
