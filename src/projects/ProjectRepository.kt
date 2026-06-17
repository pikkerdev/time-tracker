package projects

import customers.Customer
import db.CrudRepository
import db.Id
import db.json
import klite.Decimal
import klite.i18n.Lang.jsonMapper
import klite.jdbc.*
import klite.json.parse
import klite.toValues
import projects.ProjectMember.Status.DELETED
import users.User
import java.sql.ResultSet
import javax.sql.DataSource

class ProjectRepository(db: DataSource): CrudRepository<Project>(db, "projects") {
  override val selectFrom = "$table join customers c on customerId = c.id"
  override val orderAsc = "order by $table.name"
  override val defaultOrder get() = orderAsc

  private val notDeletedMember = "pm.status" neq DELETED
  private val notDeleted = "$table.status" neq DELETED
  private val notCustomer = ProjectMember::role neq ProjectMember.Role.CUSTOMER

  override fun ResultSet.mapper() = create(
    Project::hourlyRates to jsonMapper.parse<Map<ProjectMember.Role, Decimal>>(getString("hourlyRates")),
    Project::customerName to getString("c.name")
  )
  override fun Project.persister() = toValues(Project::hourlyRates to json(hourlyRates), skip = listOf(Project::customerName))

  fun forMember(userId: Id<User>, noCustomer: Boolean, includeDeleted: Boolean = false): List<Project> =
    db.select("$selectFrom join project_members pm on $table.id = pm.projectId",
      ProjectMember::userId eq userId, notDeletedMember,
      if (!includeDeleted) notDeleted else null,
      if (noCustomer) notCustomer else null, suffix = defaultOrder) { mapper() }

  fun byCustomer(customerId: Id<Customer>): List<Project> = list(Project::customerId eq customerId)

  fun listNotDeleted(): List<Project> = list(Project::status neq DELETED)

  fun delete(id: Id<Project>) {
    db.update(table, mapOf(Project::status to DELETED), Project::id to id)
  }
}
