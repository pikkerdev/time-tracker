<script lang="ts">
  import Modal from 'src/components/Modal.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import type {ProjectContext} from 'src/pages/projects/context'
  import SortableTable from 'src/components/SortableTable.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import type {Id, ProjectMember, ProjectMemberUser, User} from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'

  export let projectContext: ProjectContext

  let users: User[] = []
  let show = false
  let newMemberId: Id<User>

  async function onclick() {
    show = true
    users = await api.get('users')
  }

  async function submit() {
    await api.post(`projects/${projectContext.id}/members`, newMemberId)
    showToast(t.general.saved)
    show = false
  }

  async function deleteMember(memberId: Id<ProjectMember>) {
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`projects/member/${memberId}`)
      const updatedMembers: Record<Id<User>, ProjectMemberUser> = {}
      for (const userId in projectContext.members) {
        if (projectContext.members[userId].id !== memberId) {
          updatedMembers[userId] = projectContext.members[userId]
        }
      }
      projectContext.members = updatedMembers
      showToast(t.general.deleted)
    }
  }

</script>

<Button label={t.members.members} {onclick}/>

<Modal bind:show title={t.members.members} wide>
  <SortableTable labels={t.users} columns={[
    [t.users.name, m => m.user.firstName],
    [t.users.email, m => m.user.email],
    [t.users.role, m => m.role],
    ['', '']
    ]
    } items={Object.values(projectContext.members)} let:item={m}>
    <tr>
      <td>{m.user.firstName + ' ' + m.user.lastName}</td>
      <td>{m.user.email}</td>
      <td>{m.role}</td>
      <td> <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteMember(m.id)}/>
      </td>
    </tr>
  </SortableTable>
  <div>
    <SelectField label={t.members.addMember} bind:value={newMemberId}
                 options={users.filter(u => !Object.keys(projectContext.members).includes(u.id)).map(c => [c.id, c.firstName + ' ' + c.lastName]).toObject()}
                 emptyOption=""/>
    <Button label={t.members.addMember} onclick={submit} />
  </div>
</Modal>
