<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t, today, toISODate} from 'i18n'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import type {Id, LocalDate, TimeEntry, TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import TimeEntryCard from 'src/pages/entries/entry/TimeEntryCard.svelte'
  import TimeEntryCalendar from 'src/pages/entries/TimeEntryCalendar.svelte'
  import Modal from 'src/components/Modal.svelte'
  import {showToast} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'

  let date = today
  let timeEntries: TimeEntryView[] = []
  let timeEntry: TimeEntry = {date} as TimeEntry
  let timeEntryHours: Record<LocalDate, Record<string, number>> = {}

  let editingTimeEntry: TimeEntry | false = false

  let dates = Array.from({length: 28}, (_, i) =>
    toISODate(new Date(), d => d.setDate(d.getDate() - i))).toReversed()

  async function loadEntries(date: string) {
    editingTimeEntry = false
    timeEntries = await api.get(`timeentries?myTimeEntries=true&from=${date}`)
    timeEntryHours = await api.get(`timeentries/user?from=${dates[0]}`)
  }

  function handleEdit(entry: TimeEntryView) {
    editingTimeEntry = entry.entry
  }

  async function handleDelete(id: Id<TimeEntry>) {
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`timeentries/${id}`)
      showToast(`${t.general.deleted}`)
      timeEntries = timeEntries.filter(i => i.entry.id !== id)
      timeEntryHours
    }
  }

  $: timeEntry.date = date
  $: loadEntries(date)
</script>

<MainPageLayout class="flex flex-col gap-4 lg:gap-8">
  <div class="flex flex-col gap-4 items-center max-w-full">
    <TimeEntryCalendar bind:date {dates} {timeEntryHours}/>
    <div class="w-full flex flex-col-reverse items-center gap-4 lg:grid lg:grid-cols-5 lg:items-start lg:gap-22 px-6">
      <div class="flex flex-col gap-2 lg:col-span-3 pb-3">
        {#each timeEntries as entry (entry.entry.id)}
          <TimeEntryCard {entry}>
            <Button title={t.general.edit} icon="pencil" onclick={() => handleEdit(entry)}/>
            <Button title={t.general.delete} iconClass="text-red-500" icon="trash" onclick={() => handleDelete(entry.entry.id)}/>
          </TimeEntryCard>
        {:else}
          <div class="text-center py-4 text-muted">{t.timeEntries.noEntries}</div>
        {/each}
      </div>
      <div class="max-lg:max-w-130 lg:col-span-2">
        <TimeEntryForm bind:timeEntry onSaved={() => loadEntries(date)}/>
      </div>
    </div>
  </div>
</MainPageLayout>

<Modal bind:show={editingTimeEntry} title={t.timeEntries.title}>
  {#if editingTimeEntry}
    <TimeEntryForm bind:timeEntry={editingTimeEntry} onSaved={() => loadEntries(date)}/>
  {/if}
</Modal>
