<script lang="ts">
  import Form from 'src/forms/Form.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import {t} from 'i18n'
  import type {LocalDate, Project, TimeEntry} from 'src/api/types'
  import {onMount} from 'svelte'
  import FormField from 'src/forms/FormField.svelte'
  import NumberField from 'src/forms/NumberField.svelte'
  import Button from 'src/components/Button.svelte'
  import TextAreaField from 'src/forms/TextAreaField.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'

  const LAST_PROJECT_KEY = 'lastProjectId'

  export let timeEntry: TimeEntry
  export let onSaved: (timeEntry: TimeEntry) => void = () => {}

  let projects: Project[] = []

  async function submit() {
    timeEntry = await api.post('projects/timeentries', timeEntry)
    localStorage.setItem(LAST_PROJECT_KEY, timeEntry.projectId)
    showToast(t.general.saved)
    timeEntry = {date: timeEntry.date, projectId: timeEntry.projectId} as TimeEntry
    onSaved(timeEntry)  }

  onMount(
    async function loadProjects()  {
      projects = await api.get('projects?myProjects=true')
      projects.sort((a,b) => a.customerName!.localeCompare(b.customerName!) || a.name.localeCompare(b.name))
    })
</script>

<Form {submit} class="min-w-1/4 max-w-96 spaced">
  <SelectField label={t.projects.project} bind:value={timeEntry.projectId} options={projects.map(p => [p.id, p.customerName? `${p.customerName} ${p.name}` : p.name]).toObject()}/>
  <NumberField label={t.timeEntries.hours} bind:value={timeEntry.hours}/>
  <FormField label={t.timeEntries.storyId} required={false} bind:value={timeEntry.storyId}/>
  <TextAreaField label={t.timeEntries.description} required={false} bind:value={timeEntry.description}/>
  <Button type="submit" label={t.general.save} class="primary"/>
</Form>
