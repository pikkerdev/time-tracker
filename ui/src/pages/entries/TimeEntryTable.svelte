<script lang="ts">
  import type {Id, TimeEntry, TimeEntryView} from 'src/api/types'
  import {formatAmount, formatDate, t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Button from 'src/components/Button.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'

  export let timeEntries: TimeEntryView[]
  export let narrow = false
  export let onSaved: () => void = () => {}
  export let selectedEntryIds: string[] = []
  export let projectId: string | undefined = undefined

  let timeEntry: TimeEntryView
  let show = false

  $: if (!narrow && timeEntries && projectId) {
    selectedEntryIds = timeEntries.filter(e => !e.entry.invoiceId).map(e => e.entry.id)
  }

  async function deleteEntry(id: Id<TimeEntry>){
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`timeentries/${id}`)
      showToast(`${t.general.deleted}`)
      onSaved()
    }
  }

  function toggleEntry(entryId: string) {
    selectedEntryIds = selectedEntryIds.includes(entryId)
      ? selectedEntryIds.filter(id => id !== entryId)
      : [...selectedEntryIds, entryId]
  }
</script>

<SortableTable labels={t.timeEntries} columns={[
      [t.customers.customer, e => e.customerName],
      [t.projects.project, e => e.projectName],
      !narrow && [t.users.name, e => e.userName],
      !narrow && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.activity, e => e.entry.activity],
      [t.timeEntries.description, e => e.entry.description],
      !narrow && [t.projects.hourlyRate, e => e.entry.hourlyRate],
      !narrow && [t.timeEntries.invoiceId, e => e.entry.invoiceId],
      ''
    ]} rightAlign={[t.timeEntries.hours, t.projects.hourlyRate]}
   items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    {#if !narrow}
      <td>{e.userName}</td>
      <td>{formatDate(e.entry.date)}</td>
    {/if}
    <td class="text-right">{e.entry.hours}</td>
    <td>{e.entry.activity}</td>
    <td>{e.entry.description}</td>
    {#if !narrow}
      <td class="text-right">{formatAmount(e.entry.hourlyRate)}</td>
    {/if}
    {#if !narrow}
      <td>
        {#if e.entry.invoiceId}
          <a href="/invoices/{e.entry.invoiceId}">{e.entry.invoiceId}</a>
        {:else if projectId}
            <input type="checkbox" checked={selectedEntryIds.includes(e.entry.id)} onchange={() => toggleEntry(e.entry.id)} class="h-4 w-4 text-primary-500 border-gray-300 rounded focus:ring-primary-500">
        {/if}
      </td>
      {/if}
      <td>
        <div class="flex gap-3 justify-end">
          <Button label={t.general.edit} disabled={!!e.entry.invoiceId} onclick={() => {timeEntry = e; show = true}}/>
          <Button icon="trash" disabled={!!e.entry.invoiceId} onclick={() => {deleteEntry(e.entry.id)}}/>
        </div>
      </td>
  </tr>
</SortableTable>

<Modal title={t.timeEntries.title} bind:show>
  <TimeEntryForm bind:timeEntry={timeEntry.entry} {onSaved} bind:show/>
</Modal>
