<script lang="ts">
  import {formatAmount, formatDate, t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import type {InvoiceId, InvoiceView} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'

  let invoices: InvoiceView[]

  onMount(async () => {
    invoices = await api.get('invoices')
  })

  async function del(id: InvoiceId) {
    if (confirm(t.general.deleteConfirm)) {
      const res = await api.delete(`invoices/${id}`)
      if (res) {
        invoices = invoices.filter(i => i.invoice.id !== id)
        showToast(`${t.general.deleted} ${t.invoices.invoice} ${t.general.withId}: ${id}`)
      }
    }
  }

</script>

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <SortableTable items={invoices} columns={[
    [t.invoices.id, i => i.invoice.id],
    [t.invoices.project, i => `${i.customerName} - ${i.projectName}`],
    [t.invoices.date, i => i.invoice.date],
    [t.invoices.dueDate, i => i.invoice.dueDate],
    [t.invoices.description, i => i.invoice.description],
    [t.invoices.amountWithVat, i => i.invoice.totalAmount],
    [t.invoices.createdBy, i => i.creatorName],
    ''
    ]} let:item={i}>
    <tr>
      <td>{i.invoice.id}</td>
      <td>{i.customerName} - {i.projectName}</td>
      <td>{formatDate(i.invoice.date)}</td>
      <td>{formatDate(i.invoice.dueDate)}</td>
      <td>{i.invoice.description}</td>
      <td>{formatAmount(i.invoice.amount)} ({formatAmount(i.invoice.totalAmount)})</td>
      <td>{i.creatorName}</td>
      <td><Button icon="trash" onclick={() => del(i.invoice.id)}/></td>
    </tr>
  </SortableTable>
</MainPageLayout>

