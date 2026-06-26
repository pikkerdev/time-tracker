<script lang="ts">
  import {formatAmount, formatDate, t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import Button from 'src/components/Button.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import SortableTable from 'src/components/SortableTable.svelte'
  import type {InvoiceView} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'

  let invoices: InvoiceView[]

  onMount(async () => {
    invoices = await api.get('invoices')
  })
</script>

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <div slot="after-title">
    <Button onclick={() => navigate('/invoices/create')}>{t.invoices.create}</Button>
  </div>

  <SortableTable items={invoices} columns={[
    [t.invoices.project, i => `${i.customerName} - ${i.projectName}`],
    [t.invoices.date, i => i.invoice.date],
    [t.invoices.dueDate, i => i.invoice.dueDate],
    [t.invoices.description, i => i.invoice.description],
    [t.invoices.amountWithVat, i => i.invoice.totalAmount],
    [t.invoices.createdBy, i => i.creatorName],
    ''
    ]} let:item={i}>
    <tr>
      <td>{i.customerName} - {i.projectName}</td>
      <td>{formatDate(i.invoice.date)}</td>
      <td>{formatDate(i.invoice.dueDate)}</td>
      <td>{i.invoice.description}</td>
      <td>{formatAmount(i.invoice.amount)} ({formatAmount(i.invoice.totalAmount)})</td>
      <td>{i.creatorName}</td>
      <td></td>
    </tr>
  </SortableTable>
</MainPageLayout>

