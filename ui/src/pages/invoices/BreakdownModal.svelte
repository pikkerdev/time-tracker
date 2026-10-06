<script lang="ts">
  import {formatAmount, t} from 'i18n'
  import Modal from 'src/components/Modal.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {type InvoiceView} from 'src/api/types'
  import {groupByCustomer} from 'src/pages/invoices/invoiceUtils'

  export let invoices: InvoiceView[] = []
  export let show: 'unpaid' | 'overdue' | false = false

  let selectedProjects = new Set<string>()

  const projectKey = (customerName: string, projectName: string) => `${customerName}\u0000${projectName}`

  $: customerRows = groupByCustomer(invoices).map(c => ({
    ...c,
    projects: c.projects.map(p => ({...p, checked: selectedProjects.has(projectKey(c.customerName, p.projectName))}))
  }))

  $: selectedTotal = customerRows.reduce((sum, c) =>
    sum + c.projects.filter(p => p.checked).reduce((s, p) => s + p.total, 0), 0)

  $: if (!show) selectedProjects = new Set()

  function toggleProject(customerName: string, projectName: string, checked: boolean) {
    const key = projectKey(customerName, projectName)
    if (checked) selectedProjects.add(key)
    else selectedProjects.delete(key)
    selectedProjects = new Set(selectedProjects)
  }

  function toggleCustomer(customerName: string, projects: {projectName: string}[], checked: boolean) {
    for (const p of projects) toggleProject(customerName, p.projectName, checked)
  }

  function indeterminate(node: HTMLInputElement, value: boolean) {
    node.indeterminate = value
    return {update: (v: boolean) => node.indeterminate = v}
  }
</script>

<Modal title={show === 'overdue' ? t.invoices.overdue : t.invoices.unpaid} bind:show>
  {#if show}
    {#if selectedTotal > 0}
      <div class="mb-3 text-sm">{t.invoices.selected}: <strong>{formatAmount(selectedTotal)}</strong></div>
    {/if}
    <SortableTable items={customerRows} columns={[
      [t.customers.customer, c => c.customerName],
      [t.invoices.amount, c => c.total],
      ''
    ]} let:item={c}>
      <tr class="font-medium">
        <td>{c.customerName}</td>
        <td>{formatAmount(c.total)}</td>
        <td>
          <input type="checkbox"
                 checked={c.projects.length > 0 && c.projects.every(p => p.checked)}
                 use:indeterminate={c.projects.some(p => p.checked) && !c.projects.every(p => p.checked)}
                 onchange={e => toggleCustomer(c.customerName, c.projects, (e.currentTarget as HTMLInputElement).checked)}>
        </td>
      </tr>
      {#each c.projects as p}
        <tr>
          <td><span class="ml-8 text-gray-500">{p.projectName}</span></td>
          <td class="text-gray-500">{formatAmount(p.total)}</td>
          <td>
            <input type="checkbox" checked={p.checked}
                   onchange={e => toggleProject(c.customerName, p.projectName, (e.currentTarget as HTMLInputElement).checked)}>
          </td>
        </tr>
      {/each}
    </SortableTable>
  {/if}
</Modal>
