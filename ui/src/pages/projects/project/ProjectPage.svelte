<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {AuthRole, type Id, type Project, type ProjectMemberUser} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import NewProjectButton from 'src/pages/projects/NewProjectButton.svelte'

  import type {ProjectContext} from 'src/pages/projects/context'
  import ProjectMembersModal from 'src/pages/projects/project/ProjectMembersModal.svelte'
  import {user} from 'src/stores/auth'

  export let id: Id<Project>

  let projectContext: ProjectContext | undefined
  let project = projectContext as unknown as Project

  onMount(async () => {
    projectContext = await api.get('projects/' + id)
    api.get<ProjectMemberUser[]>(`projects/${id}/members`).then(r => {
      projectContext!.members = r.indexBy(m => m.user.id)
    })
  })
</script>

<MainPageLayout class="relative">
  {#if projectContext}
    {#if $user.authRole === AuthRole.ADMIN}
      <div class="flex justify-end">
        <ProjectMembersModal {projectContext}/>
        <NewProjectButton project={project} label={t.projects.edit}/>
      </div>
    {/if}
    <h2 class="text-2xl font-bold mb-4">{projectContext?.name}</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div>
        <p class="text-sm text-gray-500">{t.customers.customer}</p>
        <p class="text-lg font-medium">{projectContext.customerName}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.description}</p>
        <p class="text-lg font-medium">{projectContext.description}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.hourlyRate}</p>
        <p class="text-lg font-medium">{projectContext.hourlyRate}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.storyTrackerId}</p>
        <p class="text-lg font-medium">{projectContext.storyTrackerId}</p>
      </div>
    </div>
  {/if}
</MainPageLayout>
