<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, ProjectMemberRole, type ProjectMemberUser, ProjectStatus} from 'src/api/types'
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
  import Icon from 'src/icons/Icon.svelte'

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

<MainPageLayout class="relative">
  <div slot="title">
    <div class="flex items-center gap-2">
      <h1>{project?.customerName} - {project?.name}</h1>
      {#if project?.status === ProjectStatus.DELETED}
        <span class="text-muted">({t.general.deleted.toUpperCase()})</span>
      {/if}
    </div>
    {#if project?.storyTrackerId}
      <a class="flex items-center gap-1 group" href="https://story.pikker.dev/projects/{project.storyTrackerId}"
         target="_blank">
        <Icon name="storytracker" class="group-hover:rotate-180 transition-all duration-300"/>
        <span class="text-sm text-muted group-hover:text-blue-600 trainsiton-colors duration-300">{project.storyTrackerId}</span>
      </a>
    {/if}
  </div>
  <div class="flex flex-wrap gap-4" slot="after-title">
    {#if project && $user.isAdmin}
      <ProjectMembersModal {project}/>
      <ProjectFormModal bind:project label={t.projects.edit}/>
      <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteProject(id)}/>
    {/if}
  </div>
  <div class="flex flex-col gap-2 mt-4">
    <p class="text-lg">{project?.description}</p>
    <div class="flex flex-col flex-wrap gap-x-16 gap-y-4 md:flex-row">
      <div class="flex flex-col w-fit">
        <span class="text-lg font-semibold">{t.projects.hourlyRates}</span>
        <hr class="text-pikker-gold border-t-3">
        <div class="grid grid-cols-2 gap-x-4">
          {#each Object.values(ProjectMemberRole) as role}
            {@const rate = project?.hourlyRates[role]}
            {#if rate}
              <span class="justify-start">{t.members.roles[role]}</span>
              <span>{formatAmount(rate, project?.currency)}</span>
            {/if}
          {/each}
        </div>
      </div>
      <div class="flex flex-col max-w-110">
        <span class="text-lg font-semibold">{t.timeEntries.activities}</span>
        <hr class="text-pikker-gold border-t-3">
        <p>{project?.activities?.map(a => a.split(',')).join(", ")}</p>
      </div>
    </div>
  </div>
</MainPageLayout>
