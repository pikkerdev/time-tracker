<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import {t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'

  export let timeEntries: TimeEntryView[]

</script>

<SortableTable columns={[
      [t.projects.project, e => e.customerName],
      [t.projects.project, e => e.projectName],
      [t.users.name, e => e.userName],
      [t.timeEntries.date, e => e.timeEntry.date],
      [t.timeEntries.hours, e => e.timeEntry.hours],
      [t.timeEntries.storyId, e => e.timeEntry.storyId]
      ]
    }
  ]} items={timeEntries} let:item={e}>
  <tr>
    <td>{e.customerName}</td>
    <td>{e.projectName}</td>
    <td>{e.userName}</td>
    <td>{e.timeEntry.date}</td>
    <td>{e.timeEntry.hours}</td>
    <td>{e.timeEntry.storyId}</td>
  </tr>
</SortableTable>


