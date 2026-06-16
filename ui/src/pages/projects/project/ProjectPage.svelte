<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, ProjectMemberRole, type ProjectMemberUser} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {formatAmount, t} from 'i18n'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'

  import type {ProjectContext} from 'src/pages/projects/context'
  import ProjectMembersModal from 'src/pages/projects/project/ProjectMembersModal.svelte'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'
  import {navigate} from '@keksworks/svelte-tiny-router'

  export let id: Id<Project>

  let project: ProjectContext | undefined

  onMount(async () => {
    project = await api.get('projects/' + id)
    api.get<ProjectMemberUser[]>(`projects/${id}/members`).then(r => {
      project!.members = r.indexBy(m => m.user.id)
    })
  })

  async function deleteProject(Id: Id<Project>) {
    if (confirm(t.general.deleteConfirm)) await api.delete(`projects/${Id}`)
    navigate(`/projects`)
    showToast(`${t.general.deleted} ${project?.name}`)
  }
</script>

<MainPageLayout class="relative" title="{project?.name}">
  <div slot="after-title" class="flex justify-end gap-4">
    {#if project && $user.isAdmin}
      <ProjectMembersModal {project}/>
      <ProjectFormModal bind:project label={t.projects.edit}/>
      <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteProject(id)}/>
    {/if}
  </div>
  {#if project}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-6">
      <div>
        <p class="text-sm text-gray-500">{t.customers.customer}</p>
        <p class="text-lg font-medium">{project.customerName}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.description}</p>
        <p class="text-lg font-medium">{project.description}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500 w-20 text-right">{t.projects.hourlyRates}</p>
        {#each Object.values(ProjectMemberRole) as role}
        {@const rate = project.hourlyRates[role]}
        {#if rate}
          <div class="flex gap-8 justify-end max-w-40">
            <span class="justify-start">{t.members.roles[role]}</span>
            <span>{formatAmount(rate, project.currency)}</span>
          </div>

        {/if}
      {/each}
    </div>
      <div>
        <p class="text-sm text-gray-500">{t.projects.storyTrackerId}</p>
        <p class="text-lg font-medium">{project.storyTrackerId}</p>
      </div>
      <div>
        <p class="text-sm text-gray-500">{t.timeEntries.activities}</p>
        <p class="text-lg font-medium">{project.activities}</p>
      </div>
    </div>
    <div>
      <p class="text-sm text-gray-500">{t.projects.status}</p>
      <p class="text-lg font-medium">{project.status}</p>
    </div>
  {/if}
</MainPageLayout>
