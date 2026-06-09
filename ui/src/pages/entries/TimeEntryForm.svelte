<script lang="ts">
  import Form from 'src/forms/Form.svelte'
  import {t, today} from 'i18n'
  import type {Project, TimeEntry} from 'src/api/types'
  import FormField from 'src/forms/FormField.svelte'
  import NumberField from 'src/forms/NumberField.svelte'
  import Button from 'src/components/Button.svelte'
  import TextAreaField from 'src/forms/TextAreaField.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import ProjectSelect from 'src/pages/projects/ProjectSelect.svelte'
  import EntryTagsEditor from 'src/pages/entries/EntryTagsEditor.svelte'

  const LAST_PROJECT_KEY = 'lastProjectId'

  export let timeEntry: TimeEntry
  export let onSaved: () => void = () => {}
  export let show = false

  let isNew = !timeEntry.id
  let project: Project

  async function submit() {
    timeEntry = await api.post('projects/timeentries' + (isNew ? '' : '/' + timeEntry.id), timeEntry)
    if (timeEntry.tag) project.tags.push(timeEntry.tag)
    await api.post(`projects/${project.id}`, project)
    localStorage.setItem(LAST_PROJECT_KEY, timeEntry.projectId)
    showToast(t.general.saved)
    timeEntry = {date: timeEntry.date, projectId: timeEntry.projectId} as TimeEntry
    onSaved()
    show = false
  }

</script>

<Form {submit}>
  <ProjectSelect bind:projectId={timeEntry.projectId} bind:project showLastProject={true} localStorageKey={LAST_PROJECT_KEY}/>
  <NumberField label={t.timeEntries.hours} bind:value={timeEntry.hours} step={0.1}/>
  {#if !isNew}
    <FormField label={t.timeEntries.date} type="date" bind:value={timeEntry.date} max={today}/>
  {/if}
  <EntryTagsEditor label={t.timeEntries.tag} bind:tag={timeEntry.tag} projectTags={project?.tags}/>
  <FormField label={t.timeEntries.storyId} required={false} bind:value={timeEntry.storyId}/>
  <TextAreaField label={t.timeEntries.description} required={false} bind:value={timeEntry.description}/>
  <Button type="submit" label={t.general.save} class="primary"/>
</Form>
