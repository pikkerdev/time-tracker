<script lang="ts">
  import Modal from 'src/components/Modal.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import type {ProjectContext} from 'src/pages/projects/context'
  import SortableTable from 'src/components/SortableTable.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import {
    type Id,
    type ProjectMember,
    type ProjectMemberRequest,
    ProjectMemberRole,
    type ProjectMemberUser,
    type User
  } from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import Form from 'src/forms/Form.svelte'

  export let project: ProjectContext

  let users: User[] = []
  let show = false
  let userId: Id<User>

  async function onclick() {
    show = true
    users = await api.get('users')
  }

  async function submit() {
    return save({userId})
  }

  async function changeMemberRole(m: ProjectMember) {
    return save({userId: m.userId, role: m.role})
  }

  async function save(req: Partial<ProjectMemberRequest>) {
    const member = await api.post<ProjectMemberUser>(`projects/${project.id}/members`, req)
    project.members[member.user.id] = member
    showToast(t.general.saved)
  }

  async function deleteMember(memberId: Id<ProjectMember>) {
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`projects/member/${memberId}`)
      const updatedMembers: Record<Id<User>, ProjectMemberUser> = {}
      for (const userId in project.members) {
        if (project.members[userId].member.id !== memberId) {
          updatedMembers[userId] = project.members[userId]
        }
      }
      project.members = updatedMembers
      showToast(t.general.deleted)
    }
  }
</script>

<Button label={t.members.title} {onclick}/>

<Modal bind:show title={t.members.title} wide>
  <SortableTable labels={t.users} columns={[
    [t.users.name, m => m.user.name],
    [t.users.email, m => m.user.email],
    [t.users.role, m => m.member.role],
    ['', '']
  ]} items={Object.values(project.members)} let:item={m}>
    <tr>
      <td>{m.user.name}</td>
      <td>{m.user.email}</td>
      <td>
        <SelectField bind:value={m.member.role} options={t.members.roles} onchange={() => changeMemberRole(m.member)}/>
      </td>
      <td>
        <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteMember(m.member.id)}/>
      </td>
    </tr>
  </SortableTable>
  <div class="sm:flex flex-1 items-end gap-x-2 gap-y-0.5 py-2">
    <Form {submit}>
      <SelectField label={t.members.addMember} bind:value={userId}
                   options={users.filter(u => !Object.keys(project.members).includes(u.id)).map(u => [u.id, u.name]).toObject()}
                   emptyOption=""/>
      <Button type="submit" label={t.members.addMember}/>
    </Form>
  </div>
</Modal>
