<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {
    type TimeEntryView
  } from 'src/api/types'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false

  const formatDate = (date: Date) => new Intl.DateTimeFormat('en-CA').format(date)
  const now = new Date()
  const today = formatDate(now)

  let firstDayOfMonth = formatDate(new Date(now.getFullYear(), now.getMonth(), 1))
  let selectedMonth= ''
  let from = firstDayOfMonth
  let to = today

  $: if (selectedMonth) {
    const [year, month] = selectedMonth.split('-').map(Number)
    from = formatDate(new Date(year, month - 1, 1))
    to = formatDate(new Date(year, month, 0))
  } else {
    from = firstDayOfMonth
    to = today }

  function handleDateChange() {
    selectedMonth = ''
  }

  async function loadEntries(from: string, to: string, myTimeEntries: boolean) {
    let url = `projects/timeentries?myTimeEntries=${myTimeEntries}`
    if (from) url += `&from=${from}`
    if (to) url += `&to=${to}`
    timeEntries = await api.get(url)
  }

  $: loadEntries(from, to, myTimeEntries)
</script>

<MainPageLayout class="relative spaced" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    <span>{t.timeEntries.chooseMonth}</span>
    <FormField title={t.timeEntries.chooseMonth} type="month" bind:value={selectedMonth} max={today.slice(0, 7)}/>
    <FormField title={t.timeEntries.fromDate} type="date" on:input={handleDateChange} bind:value={from} max={to || today}/> -
    <FormField title={t.timeEntries.toDate} type="date" on:input={handleDateChange} bind:value={to} min={from} max={today}/>
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input title={t.timeEntries.showMyTimeEntries} type="checkbox" bind:checked={myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
