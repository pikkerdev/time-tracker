<script lang="ts">
  import type {TimeEntry} from 'src/api/types'
  import {t} from 'i18n'
  import SortableTable from 'src/components/SortableTable.svelte'

  export let timeEntries: TimeEntry[]

</script>

<SortableTable columns={[
      [t.projects.project, e => e.projectId],
      [t.users.name, e => e.userId],
      [t.timeEntries.date, e => e.date],
      [t.timeEntries.hours, e => e.hours],
      [t.timeEntries.storyId, e => e.storyId]
      ]
    }
  ]} items={timeEntries} let:item={e}>
  <tr>
    <td>{e.projectId}</td>
    <td>{e.userId}</td>
    <td>{e.date}</td>
    <td>{e.hours}</td>
    <td>{e.storyId}</td>
  </tr>
</SortableTable>


