<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, type ProjectDto, type ProjectMemberUser} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'

  import type {ProjectContext} from 'src/pages/projects/context'
  import ProjectMembersModal from 'src/pages/projects/project/ProjectMembersModal.svelte'
  import {user} from 'src/stores/auth'

  export let id: Id<Project>

  let projectContext: ProjectContext | undefined
  $: project = projectContext as unknown as ProjectDto

  onMount(async () => {
    projectContext = await api.get('projects/' + id)
    api.get<ProjectMemberUser[]>(`projects/${id}/members`).then(r => {
      projectContext!.members = r.indexBy(m => m.user.id)
    })
  })
</script>

<MainPageLayout class="relative">
  {#if projectContext}
    {#if $user.isAdmin}
      <div class="flex justify-end">
        <ProjectMembersModal {projectContext}/>
        <ProjectFormModal project={project} label={t.projects.edit}/>
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
        {#each Object.entries(projectContext.hourlyRates) as [role, rate]}
          <div class="flex gap-8 justify-between max-w-40">
            <span>{role}</span>
            <span>{rate}</span>
          </div>
        {/each}
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.storyTrackerId}</p>
        <p class="text-lg font-medium">{projectContext.storyTrackerId}</p>
      </div>
    </div>
  {/if}
</MainPageLayout>
