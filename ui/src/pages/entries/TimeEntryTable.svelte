<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {formatAmount, formatDate, t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Button from 'src/components/Button.svelte'

  export let timeEntries: TimeEntryView[]
  export let narrow = false
  export let onSaved: () => void = () => {}
  export let selectedEntryIds: string[] = []

  let timeEntry: TimeEntryView
  let show = false

  $: if (!narrow && timeEntries) {
    selectedEntryIds = timeEntries.filter(e => !e.entry.invoiceId).map(e => e.entry.id)
  }
</script>

<SortableTable labels={t.timeEntries} columns={[
      [t.customers.customer, e => e.customerName],
      [t.projects.project, e => e.projectName],
      !narrow && [t.users.name, e => e.userName],
      !narrow && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.storyId, e => e.entry.storyId],
      [t.timeEntries.description, e => e.entry.description],
      !narrow && [t.projects.hourlyRate, e => e.entry.hourlyRate],
      !narrow && [t.timeEntries.invoiceId, e => e.entry.invoiceId],
      ''
    ]} rightAlign={[t.timeEntries.hours, t.projects.hourlyRate, t.timeEntries.invoiceId]}
   items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    {#if !narrow}
      <td>{e.userName}</td>
      <td>{formatDate(e.entry.date)}</td>
    {/if}
    <td class="text-right">{e.entry.hours}</td>
    <td>{e.entry.storyId}</td>
    <td>{e.entry.description}</td>
    {#if !narrow}
      <td class="text-right">{formatAmount(e.entry.hourlyRate)}</td>
    {/if}
    {#if !narrow}
      <td class="text-right">
        {#if e.entry.invoiceId}
          {e.entry.invoiceId}
        {:else}
          <input type="checkbox" bind:group={selectedEntryIds} value={e.entry.id}>
        {/if}
      </td>
    {/if}
    <td><Button label={t.general.edit} onclick={() => {timeEntry = e; show = true}}/></td>
  </tr>
</SortableTable>

<Modal title={t.timeEntries.title} bind:show>
  <TimeEntryForm bind:timeEntry={timeEntry.entry} {onSaved} bind:show/>
</Modal>
