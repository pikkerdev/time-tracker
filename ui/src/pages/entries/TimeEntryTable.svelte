<script lang="ts">
  import type {Id, TimeEntry, TimeEntryView} from 'src/api/types'
  import {formatAmount, formatDate, t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Button from 'src/components/Button.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import Icon from 'src/icons/Icon.svelte'
  import {user} from 'src/stores/auth'

  export let timeEntries: TimeEntryView[]
  export let narrow = false
  export let onSaved: () => void = () => {}
  export let selectedEntryIds: string[] = []
  export let selectEntries = false

  let timeEntry: TimeEntryView
  let show = false

  $: if (!narrow && timeEntries && selectEntries) {
    selectedEntryIds = timeEntries.filter(e => !e.entry.invoiceId).map(e => e.entry.id)
  }
  $: if (!selectEntries){
    selectedEntryIds = []
  }

  async function deleteEntry(id: Id<TimeEntry>){
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`timeentries/${id}`)
      showToast(`${t.general.deleted}`)
      timeEntries = timeEntries.filter(i => i.entry.id !== id)
    }
  }

  function toggleEntry(entryId: string) {
    selectedEntryIds = selectedEntryIds.includes(entryId)
      ? selectedEntryIds.filter(id => id !== entryId)
      : [...selectedEntryIds, entryId]
  }
</script>

<SortableTable labels={t.timeEntries} columns={[
      [t.projects.project, e => `${e.customerName} - ${e.projectName}`],
      !narrow && [t.users.name, e => e.userName],
      !narrow && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.activity, e => e.entry.activity],
      [t.timeEntries.description, e => e.entry.description],
      !narrow && [t.projects.hourlyRate, e => e.entry.hourlyRate],
      ''
    ]} rightAlign={[t.timeEntries.hours, t.projects.hourlyRate]}
   items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName} - {e.projectName}</td>
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
      <td>
        <div class="flex gap-3 justify-end items-center">
          {#if e.entry.invoiceId && !narrow && $user.isAdmin}
            <a class="btn default icon-only" href="/invoices/{e.entry.invoiceId}" target="_blank">
              <Icon name="document"/>
            </a>
          {/if}
          {#if selectEntries && !e.entry.invoiceId}
            <input type="checkbox" checked={selectedEntryIds.includes(e.entry.id)} onchange={() => toggleEntry(e.entry.id)} class="h-4 w-4 text-primary-500 border-gray-300 rounded focus:ring-primary-500">
          {/if}
          <Button label={t.general.edit} disabled={!!e.entry.invoiceId} onclick={() => {timeEntry = e; show = true}}/>
          <Button icon="trash" disabled={!!e.entry.invoiceId} onclick={() => {deleteEntry(e.entry.id)}}/>
        </div>
      </td>
  </tr>
</SortableTable>

<Modal title={t.timeEntries.title} bind:show>
  <TimeEntryForm bind:timeEntry={timeEntry.entry} {onSaved} bind:show/>
</Modal>
