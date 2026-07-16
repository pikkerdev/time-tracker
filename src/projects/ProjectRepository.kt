package projects

import customers.Customer
import db.CrudRepository
import db.Id
import db.Status
import db.Status.DELETED
import db.json
import klite.Decimal
import klite.i18n.Lang.jsonMapper
import klite.jdbc.*
import klite.json.parse
import klite.toValues
import users.User
import java.sql.ResultSet
import javax.sql.DataSource

class ProjectRepository(db: DataSource): CrudRepository<Project>(db, "projects") {
  override val selectFrom = "$table join customers c on customerId = c.id"
  override val orderAsc = "order by $table.name"
  override val defaultOrder get() = orderAsc

  private val notDeletedMember = "pm.status" neq DELETED
  private val notDeleted = "$table.status" neq DELETED

  override fun ResultSet.mapper() = create(
    Project::hourlyRates to jsonMapper.parse<Map<ProjectMember.Role, Decimal>>(getString("hourlyRates")),
    Project::customerName to getString("c.name")
  )
  override fun Project.persister() = toValues(Project::hourlyRates to json(hourlyRates), skip = listOf(Project::customerName))

  fun forMember(userId: Id<User>, includeDeleted: Boolean = false): List<Project> =
    db.select("$selectFrom join project_members pm on $table.id = pm.projectId",
      ProjectMember::userId eq userId, notDeletedMember,
      if (!includeDeleted) notDeleted else null, suffix = defaultOrder) { mapper() }

  fun byCustomer(customerId: Id<Customer>): List<Project> = list(Project::customerId eq customerId)

  fun listNotDeleted(): List<Project> =
    db.select("$selectFrom ",
      notDeleted, suffix = defaultOrder) { mapper() }

  fun setStatus(id: Id<Project>, status: Status) {
    db.update(table, mapOf(Project::status to status), Project::id to id)
  }

  fun setStatuses(id: Id<Customer>, status: Status) {
    db.update(table, mapOf(Project::status to status), Project::customerId to id)
  }
}
