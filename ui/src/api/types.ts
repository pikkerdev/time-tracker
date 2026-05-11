export type Id<T extends Entity<T>> = string & {_of?: T}
export type Entity<T extends Entity<T>> = {id: Id<T>}

// class customers.Customer
export interface Customer {businessRegistryCode?: string; id: Id<Customer>; invoiceEmail?: Email; legalAddress?: string; legalName?: string; name: string; phone?: Phone; vatId?: string}
// class project.Project
export interface Project {currency: string; customerId: Id<Customer>; description?: string; hourlyRates: Partial<Record<Role, number>>; id: Id<Project>; name: string; storyTrackerId?: number}
// class project.ProjectDto
export interface ProjectDto {currency: string; customerName: string; description?: string; hourlyRates: Partial<Record<Role, number>>; id: Id<Project>; name: string; storyTrackerId?: number}
// class project.ProjectMember
export interface ProjectMember {createdAt: Instant; id: Id<ProjectMember>; projectId: Id<Project>; role: Role; status: Status; updatedAt?: Instant; userId: Id<User>}
// class project.ProjectMemberUser
export interface ProjectMemberUser {id: Id<ProjectMember>; member: ProjectMember; role: Role; user: User}
// class project.Role
export enum Role {DEVELOPER = 'DEVELOPER', ARCHITECT = 'ARCHITECT', INTERN = 'INTERN', CUSTOMER = 'CUSTOMER'}
// class project.Status
export enum Status {ACTIVE = 'ACTIVE', DELETED = 'DELETED'}
// class project.TimeEntry
export interface TimeEntry {date: LocalDate; description?: string; hourlyRate: number; hours: number; id: Id<TimeEntry>; projectId: Id<Project>; projectMemberId: Id<ProjectMember>; storyId?: number}
// class users.AuthRole
export enum AuthRole {ADMIN = 'ADMIN', USER = 'USER', EXTERNAL = 'EXTERNAL'}
// class users.User
export interface User {authRole: AuthRole; avatarUrl?: URI; createdAt: Instant; email: Email; firstName: string; id: Id<User>; isAdmin: boolean; lastName: string; updatedAt?: Instant}

// java.time.LocalDate
export type LocalDate = `${number}-${number}-${number}`
// java.time.Instant
export type Instant = `${number}-${number}-${number}T${number}:${number}:${number}Z`
// java.net.URI
export type URI = `${string}://${string}`
// klite.Email
export type Email = `${string}@${string}`
// klite.Phone
export type Phone = `+${number}`
