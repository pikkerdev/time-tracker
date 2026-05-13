import type {Id, Project, ProjectMemberUser, User} from 'src/api/types'

export type ProjectContext = Project & {
  members: Record<Id<User>, ProjectMemberUser>
}
