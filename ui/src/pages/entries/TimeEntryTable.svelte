<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {formatDate, t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Button from 'src/components/Button.svelte'

  export let timeEntries: TimeEntryView[]
  export let isTimeEntryPage = false
  export let isInvoicePage = false
  export let onSaved: () => void = () => {}
  export let selectedEntryIds: string[] = []
  let timeEntry: TimeEntryView
  let show = false

  $: if (isInvoicePage && timeEntries) {
    selectedEntryIds = timeEntries.map(e => e.entry.id)
  }
</script>

<SortableTable columns={[
      isInvoicePage && [t.timeEntries.selected, e => e.entry.id],
      !isInvoicePage && [t.customers.customer, e => e.customerName],
      !isInvoicePage && [t.projects.project, e => e.projectName],
      isTimeEntryPage && [t.users.name, e => e.userName],
      isTimeEntryPage && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      isInvoicePage && [t.projects.hourlyRate, e => e.entry.hourlyRate],
      [t.timeEntries.storyId, e => e.entry.storyId],
      ['', '']
      ]
    } items={timeEntries} let:item={e}>
  <tr>
    <td>
      {#if !isInvoicePage} {e.customerName}
        {:else if e.entry.invoiceId} {t.invoices.invoiced}
        {:else} <input type="checkbox" bind:group={selectedEntryIds} value={e.entry.id}>
      {/if}
    </td>
    {#if !isInvoicePage}
      <td>{e.projectName}</td>
    {/if}
    {#if isTimeEntryPage}
      <td>{e.userName}</td>
      <td>{formatDate(e.entry.date)}</td>
    {/if}
    <td>{e.entry.hours}</td>
    {#if isInvoicePage}
      <td>{e.entry.hourlyRate}</td>
    {/if}
    <td>{e.entry.storyId}</td>
    {#if !isInvoicePage}
      <td><Button label={t.general.edit} onclick={() => {timeEntry = e; show = true}}/></td>
    {/if}
  </tr>
</SortableTable>

<Modal title={t.timeEntries.title} bind:show>
  <TimeEntryForm bind:timeEntry={timeEntry.entry} {onSaved} bind:show/>
</Modal>
