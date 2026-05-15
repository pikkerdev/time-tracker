<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, ProjectMemberRole, type ProjectMemberUser, type TimeEntry} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {formatAmount, t} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'

  let timeEntries: TimeEntry[]

  onMount(async () => {
    timeEntries = await api.get('projects/timeentries')
  })
</script>

<MainPageLayout class="relative" title={t.timeEntries.title}>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
