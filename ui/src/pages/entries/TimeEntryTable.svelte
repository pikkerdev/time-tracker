<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'

  export let timeEntries: TimeEntryView[]
  export let showUser = true
  export let showDate = true

</script>

<SortableTable columns={[
      [t.customers.customer, e => e.customerName],
      [t.projects.project, e => e.projectName],
      showUser && [t.users.name, e => e.userName],
      showDate && [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.storyId, e => e.entry.storyId]
      ]
    } items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    {#if showUser }
      <td>{e.userName}</td>
    {/if}
    {#if showDate}
      <td>{e.entry.date}</td>
    {/if}
    <td>{e.entry.hours}</td>
    <td>{e.entry.storyId}</td>
  </tr>
</SortableTable>


