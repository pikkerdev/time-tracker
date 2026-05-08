package project

import db.CrudRepository
import db.Id
import klite.jdbc.count
import klite.jdbc.create
import klite.jdbc.delete
import klite.jdbc.select
import users.User
import javax.sql.DataSource

class ProjectMemberRepository(db: DataSource): CrudRepository<ProjectMember>(db, "project_members") {

  fun list(projectId: Id<Project>): List<ProjectMemberUser> =
    db.select("$table join users u on userId = u.id", ProjectMember::projectId to projectId) {
      ProjectMemberUser(create(), create("u."))
    }

  fun isMember(projectId: Id<Project>, userId: Id<User>): Boolean =
    db.count(table, listOf(ProjectMember::projectId to projectId, ProjectMember::userId to userId)) > 0

  fun delete(id: Id<ProjectMember>) { db.delete(table, ProjectMember::id to id) }
}
