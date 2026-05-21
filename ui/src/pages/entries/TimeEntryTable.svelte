<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'

  export let timeEntries: TimeEntryView[]

</script>

<SortableTable columns={[
      [t.customers.customer, e => e.customerName],
      [t.projects.project, e => e.projectName],
      [t.users.name, e => e.userName],
      [t.timeEntries.date, e => e.entry.date],
      [t.timeEntries.hours, e => e.entry.hours],
      [t.timeEntries.storyId, e => e.entry.storyId]
      ]
    } items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    <td>{e.userName}</td>
    <td>{e.entry.date}</td>
    <td>{e.entry.hours}</td>
    <td>{e.entry.storyId}</td>
  </tr>
</SortableTable>


