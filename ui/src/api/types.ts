export type Entity<T extends Entity<T>> = {id: Id<T>}

// class customers.Customer
export interface Customer {businessRegistryCode?: string; id: Id<Customer>; invoiceEmail?: Email; legalAddress?: string; legalName?: string; name: string; phone?: Phone; status: Status; updatedAt?: Instant; vatId?: string}
// class db.Status
export enum Status {ACTIVE = 'ACTIVE', DELETED = 'DELETED'}
// class invoices.Invoice$Status
export enum InvoiceStatus {CREATED = 'CREATED', SENT = 'SENT', PAID = 'PAID'}
// class invoices.Invoice
export interface Invoice {date: LocalDate; description: string; dueDate: LocalDate; id: InvoiceId; projectId: Id<Project>; rows: Array<InvoiceRow>; status: InvoiceStatus; updatedAt?: Instant}
// class invoices.InvoiceCreateRequest
export interface InvoiceCreateRequest {date: LocalDate; description: string; dueDate: LocalDate; rows: Array<InvoiceRow>; timeEntryIds: Array<Id<TimeEntry>>}
// class invoices.InvoiceDetails
export interface InvoiceDetails {customer: Customer; invoice: Invoice; vat: number}
// class invoices.InvoiceId
export type InvoiceId = number
// class invoices.InvoiceRow
export interface InvoiceRow {amount: number; description: string; hours?: number; rate?: number}
// class invoices.InvoiceView
export interface InvoiceView {creatorName: string; customerName: string; invoice: Invoice; projectName: string}
// class invoices.InvoiceWithCustomer
export interface InvoiceWithCustomer {customerId: Id<Customer>; invoice: Invoice}
// class merit.CountryCode
export enum CountryCode {EE = 'EE'}
// class merit.MeritInvoice$Customer
export interface MeritInvoiceCustomer {countryCode: CountryCode; email?: Email; name: string; notTDCustomer: boolean; phoneNo?: Phone; regNo?: string}
// class merit.MeritInvoice$InvoiceRequest
export interface MeritInvoiceInvoiceRequest {addAttachment: boolean; id: string}
// class merit.MeritInvoice$InvoiceResponse
export interface MeritInvoiceInvoiceResponse {header: MeritInvoiceInvoiceResponseHeader; payments: Array<MeritInvoicePayment>}
// class merit.MeritInvoice$InvoiceResponseHeader
export interface MeritInvoiceInvoiceResponseHeader {paid: boolean}
// class merit.MeritInvoice$Item
export interface MeritInvoiceItem {code: string; description: string; type: number}
// class merit.MeritInvoice$Payment
export interface MeritInvoicePayment {amount: number; paymDate: LocalDate}
// class merit.MeritInvoice$Response
export interface MeritInvoiceResponse {customerId: string; invoiceId: string; invoiceNo: string; refNo: number}
// class merit.MeritInvoice$Row
export interface MeritInvoiceRow {amount: number; item: MeritInvoiceItem; price: number; quantity: number; taxId: string}
// class merit.MeritInvoice$TaxAmount
export interface MeritInvoiceTaxAmount {amount?: number; taxId: string}
// class merit.MeritInvoice
export interface MeritInvoice {currencyCode: string; customer: MeritInvoiceCustomer; docDate: LocalDate; dueDate: LocalDate; fComment: string; invoiceNo: string; invoiceRow: Array<MeritInvoiceRow>; taxAmount: Array<MeritInvoiceTaxAmount>; totalAmount: number; transactionDate: LocalDate}
// class projects.Project
export interface Project {activities: Array<string>; color: string; currency: string; customerId: Id<Customer>; customerName?: string; description?: string; hourlyRates: Partial<Record<ProjectMemberRole, number>>; id: Id<Project>; name: string; status: Status; storyTrackerId?: number; updatedAt?: Instant}
// class projects.ProjectMember$Role
export enum ProjectMemberRole {DEVELOPER = 'DEVELOPER', ARCHITECT = 'ARCHITECT', INTERN = 'INTERN', CUSTOMER = 'CUSTOMER'}
// class projects.ProjectMember
export interface ProjectMember {createdAt: Instant; id: Id<ProjectMember>; projectId: Id<Project>; role: ProjectMemberRole; status: Status; updatedAt?: Instant; userId: Id<User>}
// class projects.ProjectMemberRequest
export interface ProjectMemberRequest {role: ProjectMemberRole; userId: Id<User>}
// class projects.ProjectMemberUser
export interface ProjectMemberUser {member: ProjectMember; user: User}
// class projects.ProjectStats
export interface ProjectStats {totalHours: number; totalRevenue: number; unbilledHours: number; unbilledRevenue: number}
// class projects.ProjectView
export interface ProjectView {project: Project; stats: ProjectStats}
// class timeentries.TimeEntry
export interface TimeEntry {activity: string; date: LocalDate; description?: string; hourlyRate: number; hours: number; id: Id<TimeEntry>; invoiceId?: InvoiceId; projectId: Id<Project>; role: ProjectMemberRole; storyIds: Array<string>; userId: Id<User>}
// class timeentries.TimeEntryView
export interface TimeEntryView {customerName: string; entry: TimeEntry; projectName: string; userName: string}
// class timeentries.UpdateHourlyRatesRequest
export interface UpdateHourlyRatesRequest {rate: number; timeEntryIds: Array<Id<TimeEntry>>}
// class users.AuthRole
export enum AuthRole {ADMIN = 'ADMIN', INTERNAL = 'INTERNAL', EXTERNAL = 'EXTERNAL', CUSTOMER = 'CUSTOMER'}
// class users.User
export interface User {authRole: AuthRole; avatarUrl?: URI; createdAt: Instant; email: Email; firstName: string; id: Id<User>; isAdmin: boolean; isCustomer: boolean; isExternal: boolean; isInternal: boolean; lastName: string; name: string; phone?: Phone; updatedAt?: Instant}

// klite.Id
export type Id<T> = string & {_of?: T}
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
