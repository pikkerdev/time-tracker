<script lang="ts">
  import {t} from 'i18n'
  import SelectField from 'src/forms/SelectField.svelte'
  import type {Id, Project} from 'src/api/types'
  import {onMount} from 'svelte'
  import {user} from 'src/stores/auth'
  import api from 'src/api/api'
  import {isOverBudget} from 'src/pages/projects/context'

  export let projectId: Id<Project> = ''
  export let project: Project | undefined = undefined
  export let showLastProject = false
  export let localStorageKey: string | undefined = undefined

  let projects: Project[] = []

  onMount(async () => {
    if (!$user.isAdmin || showLastProject) projects = await api.get(`projects?myProjects=true`)
    else projects = await api.get('projects?myProjects=false')
    projects.sort((a,b) => a.customerName!.localeCompare(b.customerName!) || a.name.localeCompare(b.name))
    if (localStorageKey && !projectId) {
      const lastProjectId = localStorage.getItem(localStorageKey)
      if (lastProjectId) projectId = lastProjectId
    }
  })

  $: project = projects.find(p => p.id == projectId) as Project
</script>

<div>
  <SelectField bind:value={projectId} label={showLastProject? t.projects.project : undefined}
               emptyOption={!showLastProject? t.projects.all : undefined}
               options={projects.map(p => [p.id, p.customerName? `${p.customerName} - ${p.name}` : p.name]).toObject()}/>
  {#if isOverBudget(project)}
    <p class="text-sm font-medium text-red-700">{t.projects.overBudget}</p>
  {/if}
</div>
