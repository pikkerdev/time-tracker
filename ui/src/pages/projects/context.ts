import type {Id, ProjectMemberUser, ProjectView, User} from 'src/api/types'

export type ProjectContext = ProjectView & {
  members: Members
}

export type Members = Record<Id<User>, ProjectMemberUser>
