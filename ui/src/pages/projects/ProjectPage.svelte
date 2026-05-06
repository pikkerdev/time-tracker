<script lang="ts">

import MainPageLayout from 'src/layout/MainPageLayout.svelte'
import type {Id, Project, ProjectMemberUser, ProjectWithCustomer, User} from 'src/api/types'
import {onMount} from 'svelte'
import api from 'src/api/api'
import {t} from 'i18n'
import NewProjectButton from 'src/pages/projects/NewProjectButton.svelte'

import type {ProjectContext} from 'src/pages/projects/context'
import ProjectMembersModal from 'src/pages/projects/ProjectMembersModal.svelte'

export let id: Id<Project>

let projectContext: ProjectContext | undefined
let users: User[]

onMount(async () => {
  projectContext = await api.get('projects/' + id)
  api.get<ProjectMemberUser[]>(`projects/${id}/members`).then(r => {
    projectContext!.members = r.indexBy(m => m.user.id)
  })
  users = await api.get('users')
})
</script>

<MainPageLayout class="relative">
  {#if projectContext}
    <div class="flex justify-end">
      <ProjectMembersModal {projectContext} {users}/>
      <NewProjectButton project={projectContext.project} label={t.projects.edit}/>
    </div>
    <h2 class="text-2xl font-bold mb-4">{projectContext?.name}</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div>
        <p class="text-sm text-gray-500">{t.customers.customer}</p>
        <p class="text-lg font-medium">{projectContext.customer.name}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.description}</p>
        <p class="text-lg font-medium">{projectContext.project.description}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.hourlyRate}</p>
        <p class="text-lg font-medium">{projectContext.project.hourlyRate}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.storyTrackerId}</p>
        <p class="text-lg font-medium">{projectContext.project.storyTrackerId}</p>
      </div>
    </div>
  {/if}
</MainPageLayout>
