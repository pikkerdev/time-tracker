package project

import db.Entity
import db.Id
import klite.jdbc.UpdatableEntity
import klite.jdbc.nowSec
import project.Role.DEVELOPER
import project.Status.ACTIVE
import users.User
import java.time.Instant

enum class Role {
  DEVELOPER,
  ARCHITECT,
  INTERN,
  CUSTOMER
}

enum class Status {
  ACTIVE,
  DELETED
}

data class ProjectMember(
  val projectId: Id<Project>,
  val userId: Id<User>,
  val role: Role = DEVELOPER,
  override var updatedAt: Instant? = null,
  val createdAt: Instant = nowSec(),
  val status: Status = ACTIVE,
  override val id: Id<ProjectMember> = Id(),
): Entity<ProjectMember>, UpdatableEntity


data class ProjectMemberUser(
  val member: ProjectMember,
  val user: User
)
