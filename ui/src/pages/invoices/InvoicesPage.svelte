<script lang="ts">
  import {formatAmount, formatDate, t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {type InvoiceId, InvoiceStatus, type InvoiceView} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'
  import Icon from 'src/icons/Icon.svelte'

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

  async function setStatus(invoiceView: InvoiceView, status: InvoiceStatus) {
    if (confirm(t.general.deleteConfirm)) {
      const res = await api.post(`invoices/${invoiceView.invoice.id}`, `"${status}"`)
      if (res) {
        invoiceView.invoice.status = status
        invoices = invoices
        showToast(`${t.general.statusSetTo}: ${status}`)
      }
    }
  }

  function amount(invoiceView: InvoiceView): number {
    return invoiceView.invoice.rows?.reduce((sum, row) => sum + row.amount, 0) ?? 0
  }


</script>

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <SortableTable items={invoices} columns={[
    [t.invoices.id, i => i.invoice.id],
    [t.invoices.project, i => `${i.customerName} - ${i.projectName}`],
    [t.invoices.date, i => i.invoice.date],
    [t.invoices.dueDate, i => i.invoice.dueDate],
    [t.invoices.description, i => i.invoice.description],
    [t.invoices.amount, i => amount(i)],
    [t.invoices.createdBy, i => i.creatorName],
    [t.general.status, i => i.invoice.status],
    ''
    ]} let:item={i}>
    <tr>
      <td>{i.invoice.id}</td>
      <td>{i.customerName} - {i.projectName}</td>
      <td>{formatDate(i.invoice.date)}</td>
      <td>{formatDate(i.invoice.dueDate)}</td>
      <td>{i.invoice.description}</td>
      <td>{formatAmount(amount(i))}</td>
      <td>{i.creatorName}</td>
      <td>{i.invoice.status}</td>
      <td>
        <div class="flex gap-2 justify-end">
          {#if i.invoice.status === 'CREATED'}
            <Button label={t.invoices.sent} onclick={() => setStatus(i, InvoiceStatus.SENT)}/>
          {:else if i.invoice.status === 'SENT'}
            <Button label={t.invoices.paid} onclick={() => setStatus(i, InvoiceStatus.PAID)}/>
          {/if}
          <a class="btn default icon-only" href="/invoices/{i.invoice.id}" target="_blank">
            <Icon name="eye"/>
          </a>
          <Button icon="trash" disabled={!(i.invoice.status === 'CREATED')} onclick={() => del(i.invoice.id)}/>
        </div>
      </td>
    </tr>
  </SortableTable>
</MainPageLayout>

