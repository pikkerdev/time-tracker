<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {formatDate, t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Button from 'src/components/Button.svelte'

  export let timeEntries: TimeEntryView[]
  export let showUser = true
  export let showDate = true
  export let onSaved: () => void = () => {}
  let timeEntry: TimeEntryView
  let show = false

</script>

<SortableTable columns={[
      [t.customers.customer, e => e.customerName],
      [t.projects.project, e => e.projectName],
      showUser && [t.users.name, e => e.userName],
      showDate && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.storyId, e => e.entry.storyId],
      ['', '']
      ]
    } items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    {#if showUser}
      <td>{e.userName}</td>
    {/if}
    {#if showDate}
      <td>{formatDate(e.entry.date)}</td>
    {/if}
    <td>{e.entry.hours}</td>
    <td>{e.entry.storyId}</td>
    <td><Button label={t.general.edit} onclick={() => {timeEntry = e; show = true}}/></td>
  </tr>
</SortableTable>

<Modal title={t.timeEntries.title} bind:show>
  <TimeEntryForm bind:timeEntry={timeEntry.entry} {onSaved} bind:show/>
</Modal>

