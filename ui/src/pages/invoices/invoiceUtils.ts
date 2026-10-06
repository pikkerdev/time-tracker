import {type InvoiceView} from 'src/api/types'

export function amount(invoiceView: InvoiceView): number {
  return invoiceView.invoice.rows?.reduce((sum, row) => sum + row.amount, 0) ?? 0
}

export type CustomerBreakdown = {customerName: string, total: number, projects: {projectName: string, total: number}[]}

export function groupByCustomer(invoices: InvoiceView[]): CustomerBreakdown[] {
  const customers = new Map<string, CustomerBreakdown>()
  for (const i of invoices) {
    const amt = amount(i)
    let customer = customers.get(i.customerName)
    if (!customer) {
      customer = {customerName: i.customerName, total: 0, projects: []}
      customers.set(i.customerName, customer)
    }
    customer.total += amt
    let project = customer.projects.find(p => p.projectName === i.projectName)
    if (!project) {
      project = {projectName: i.projectName, total: 0}
      customer.projects.push(project)
    }
    project.total += amt
  }
  return [...customers.values()].sort((a, b) => a.customerName.localeCompare(b.customerName))
}
