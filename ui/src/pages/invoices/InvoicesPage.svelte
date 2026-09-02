<script lang="ts">
  import {formatAmount, formatDate, lang, t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {type Invoice, type InvoiceId, InvoiceStatus, type InvoiceView, type LocalDate} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'
  import Icon from 'src/icons/Icon.svelte'
  import Modal from 'src/components/Modal.svelte'
  import InvoiceForm from 'src/pages/invoices/InvoiceForm.svelte'

  const todayStr = new Date().toISOString().slice(0, 10)

  let invoices: InvoiceView[]
  let invoiceToEdit: Invoice | false = false

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
    if (confirm(`${t.invoices.setInvoiceStatusTo} ${status.toLowerCase()}?`)) {
      const res = await api.post(`invoices/${invoiceView.invoice.id}/status`, `"${status}"`)
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

  function formatDateMonth(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {year: 'numeric', month: 'short'})
  }

  function isOverdue(invoice: InvoiceView): boolean {
    return invoice.invoice.dueDate < todayStr && invoice.invoice.status !== InvoiceStatus.PAID
  }

  function onInvoiceSaved(invoice: Invoice) {
    invoices = invoices.map(i => i.invoice.id === invoice.id ? {...i, invoice} : i)
    invoiceToEdit = false
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
    [t.invoices.revenueMonth, i => i.invoice.revenueMonth],
    ''
    ]} let:item={i}>
    <tr>
      <td>{i.invoice.id}</td>
      <td>{i.customerName} - {i.projectName}</td>
      <td>{formatDate(i.invoice.date)}</td>
      <td class:text-red-500={isOverdue(i)}>{formatDate(i.invoice.dueDate)}</td>
      <td>{i.invoice.description}</td>
      <td>{formatAmount(amount(i))}</td>
      <td>{i.creatorName}</td>
      <td>{i.invoice.status}</td>
      <td>{formatDateMonth(i.invoice.revenueMonth)}</td>
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
          <Button title={t.general.edit} icon="pencil" onclick={() => invoiceToEdit = structuredClone(i.invoice)}/>
          <Button icon="trash" disabled={i.invoice.status === 'PAID'} onclick={() => del(i.invoice.id)}/>
        </div>
      </td>
    </tr>
  </SortableTable>
</MainPageLayout>

<Modal title={t.invoices.edit} bind:show={invoiceToEdit}>
  {#if invoiceToEdit}
    <InvoiceForm invoice={invoiceToEdit} onSaved={onInvoiceSaved}/>
  {/if}
</Modal>
