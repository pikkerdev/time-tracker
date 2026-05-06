package project

import customers.Customer
import db.CrudRepository
import db.Id
import klite.jdbc.create
import klite.jdbc.get
import klite.jdbc.select
import users.User
import java.sql.ResultSet
import javax.sql.DataSource

class ProjectRepository(db: DataSource): CrudRepository<Project>(db, "projects") {
  private fun ResultSet.projectDtoMapper(): ProjectDto {
    val project = create<Project>()
    return ProjectDto(
      id = project.id,
      customerName = get<String>("c.name"),
      name = project.name,
      description = project.description,
      currency = project.currency,
      hourlyRate = project.hourlyRate,
      storyTrackerId = project.storyTrackerId
    )
  }

  fun getDto(id: Id<Project>): ProjectDto? =
    db.select("$table p join customers c on p.customerId = c.id", "p.id" to id.value) {
      projectDtoMapper()
    }.firstOrNull()

  fun dtoList(): List<ProjectDto> =
    db.select("$table p join customers c on p.customerId = c.id") {
      projectDtoMapper()
    }

  fun dtoListForMember(userId: Id<User>): List<ProjectDto> =
    db.select(
      "$table p join customers c on p.customerId = c.id join project_members pm on p.id = pm.projectId",
      listOf("pm.userId" to userId.value)
    ) {
      projectDtoMapper()
    }

  fun dtoListByCustomerAndMember(customerId: Id<Customer>, userId: Id<User>): List<ProjectDto> =
    db.select(
      "$table p join customers c on p.customerId = c.id join project_members m on p.id = m.projectId",
      listOf("p.customerId" to customerId.value, "m.userId" to userId.value)
    ) {
      projectDtoMapper()
    }
}
