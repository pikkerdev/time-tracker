export type Id<T extends Entity<T>> = string & {_of?: T}
export type Entity<T extends Entity<T>> = {id: Id<T>}

// class customers.Customer
export interface Customer {businessRegistryCode?: string; id: Id<Customer>; invoiceEmail?: Email; legalAddress?: string; legalName?: string; name: string; phone?: Phone; vatId?: string}
// class invoices.Invoice
export interface Invoice {amount: number; date: LocalDate; description: string; dueDate: LocalDate; id: InvoiceId; projectId: Id<Project>; totalAmount: number; vatAmount: number}
// class invoices.InvoiceCreateRequest
export interface InvoiceCreateRequest {date: LocalDate; description: string; dueDate: LocalDate; timeEntryIds: Array<Id<TimeEntry>>}
// class invoices.InvoiceId
export type InvoiceId = number
// class projects.Project$Status
export enum ProjectStatus {ACTIVE = 'ACTIVE', DELETED = 'DELETED'}
// class projects.Project
export interface Project {activities: Array<string>; currency: string; customerId: Id<Customer>; customerName?: string; description?: string; hourlyRates: Partial<Record<ProjectMemberRole, number>>; id: Id<Project>; name: string; status: ProjectStatus; storyTrackerId?: number; updatedAt?: Instant}
// class projects.ProjectMember$Role
export enum ProjectMemberRole {DEVELOPER = 'DEVELOPER', ARCHITECT = 'ARCHITECT', INTERN = 'INTERN', CUSTOMER = 'CUSTOMER'}
// class projects.ProjectMember$Status
export enum ProjectMemberStatus {ACTIVE = 'ACTIVE', DELETED = 'DELETED'}
// class projects.ProjectMember
export interface ProjectMember {createdAt: Instant; id: Id<ProjectMember>; projectId: Id<Project>; role: ProjectMemberRole; status: ProjectMemberStatus; updatedAt?: Instant; userId: Id<User>}
// class projects.ProjectMemberRequest
export interface ProjectMemberRequest {role: ProjectMemberRole; userId: Id<User>}
// class projects.ProjectMemberUser
export interface ProjectMemberUser {member: ProjectMember; user: User}
// class timeentries.TimeEntry
export interface TimeEntry {activity?: string; date: LocalDate; description?: string; hourlyRate: number; hours: number; id: Id<TimeEntry>; invoiceId?: InvoiceId; projectId: Id<Project>; storyId?: number; userId: Id<User>}
// class timeentries.TimeEntryView
export interface TimeEntryView {customerName: string; entry: TimeEntry; projectName: string; userName: string}
// class users.AuthRole
export enum AuthRole {ADMIN = 'ADMIN', USER = 'USER', EXTERNAL = 'EXTERNAL'}
// class users.User
export interface User {authRole: AuthRole; avatarUrl?: URI; createdAt: Instant; email: Email; firstName: string; id: Id<User>; isAdmin: boolean; isUser: boolean; lastName: string; name: string; updatedAt?: Instant}

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
