<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {today, toISODate} from 'i18n'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import type {LocalDate, TimeEntry, TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import TimeEntryCalendar from 'src/pages/entries/TimeEntryCalendar.svelte'

  let date = today
  let timeEntries: TimeEntryView[] = []
  let timeEntry: TimeEntry = {date} as TimeEntry
  let timeEntryHours: Record<LocalDate, number> = {}

  let dates = Array.from({length: 28}, (_, i) =>
    toISODate(new Date(), d => d.setDate(d.getDate() - i))).toReversed()

  $: timeEntry.date = date
  $: loadEntries(date)

  async function loadEntries(date: string) {
    timeEntries = await api.get(`projects/timeentries?myTimeEntries=true&from=${date}`)
    timeEntryHours = await api.get(`projects/timeentries/user?from=${dates[0]}`)
  }
</script>

<MainPageLayout class="flex flex-col gap-4 lg:gap-8">
  <div class="flex flex-col gap-4 items-center">
    <TimeEntryCalendar bind:date {dates} {timeEntryHours}/>
    <div class="min-w-1/4 max-w-96">
      <TimeEntryForm bind:timeEntry onSaved={() => loadEntries(date)}/>
    </div>
    <div class="px-4 overflow-x-scroll max-w-full">
      <TimeEntryTable projectId={timeEntry.projectId} {timeEntries} narrow onSaved={() => loadEntries(date)}/>
    </div>
  </div>
</MainPageLayout>
