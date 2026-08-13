<script lang="ts">
  import Modal from 'src/components/Modal.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import type {Members} from 'src/pages/projects/context'
  import SortableTable from 'src/components/SortableTable.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import {
    AuthRole,
    type Id,
    type Project,
    type ProjectMember,
    type ProjectMemberRequest,
    ProjectMemberRole,
    type ProjectMemberUser,
    type User
  } from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import Form from 'src/forms/Form.svelte'
  import ProjectMemberForm from 'src/pages/projects/project/ProjectMemberForm.svelte'

  export let members: Members
  export let projectId: Id<Project>

  let users: User[] = []
  let show = false
  let email: `${string}@${string}`
  let role: ProjectMemberRole
  let userRole: AuthRole | undefined = undefined
  let addMember = false
  const roleOptions = Object.fromEntries(Object.values(ProjectMemberRole).filter(e => e !== ProjectMemberRole.CUSTOMER).map(e => [e,e]))

  async function onclick() {
    show = true
    users = await api.get('users')
  }

  async function submit() {
    userRole = undefined
    return save({email, role})
  }

  async function changeMemberRole(m: ProjectMemberUser) {
    return save({email: m.user.email, role: m.member.role})
  }

  async function save(req: Partial<ProjectMemberRequest>) {
    const member = await api.post<ProjectMemberUser>(`projects/${projectId}/members`, req)
    members[member.user.id] = member
    showToast(t.general.saved)
  }

  async function deleteMember(memberId: Id<ProjectMember>) {
    if (!confirm(t.general.deleteConfirm)) return
    await api.delete(`projects/member/${memberId}`)
    const updatedMembers: Record<Id<User>, ProjectMemberUser> = {}
    for (const userId in members) {
      if (members[userId].member.id !== memberId) {
        updatedMembers[userId] = members[userId]
      }
    }
    members = updatedMembers
    showToast(t.general.deleted)
  }

  function onSaved(member: ProjectMemberUser){
    members[member.user.id] = member
    addMember = false
  }

  $: if (email) userRole = users.find(u => u.email === email)?.authRole
</script>

<Button label={t.members.title} {onclick}/>

<Modal bind:show title={t.members.title} wide>
  <SortableTable labels={t.users} columns={[
    [t.users.name, m => m.user.name],
    [t.users.email, m => m.user.email],
    [t.users.role, m => m.member.role],
    ['', '']
  ]} items={Object.values(members)} let:item={m}>
    <tr>
      <td>{m.user.name}</td>
      <td>{m.user.email}</td>
      {#if m.user.isCustomer}
        <td>{m.member.role}</td>
      {:else}
        <td>
          <SelectField title={t.members.roles} bind:value={m.member.role} options={roleOptions} onchange={() => changeMemberRole(m)}/>
        </td>
      {/if}
      <td>
        <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteMember(m.member.id)}/>
      </td>
    </tr>
  </SortableTable>
  <div class="">
    <h6 class ="mb-4">{t.members.addMember}</h6>
    <div class="flex gap-4">
      <Form {submit} class="flex gap-4">
        <SelectField bind:value={email} placeholder={t.members.chooseMember}
                     options={users.filter(u => !members[u.id]).indexBy(u => u.email, u => `${u.name} (${u.email})`)}/>
        {#if !(userRole == 'CUSTOMER' || userRole == undefined)}
          <SelectField bind:value={role} placeholder={t.members.chooseRole} options={roleOptions}/>
        {/if}
        <Button type="submit" label={t.general.add}/>
      </Form>
      <Button label={t.members.newMember} onclick={() => addMember = !addMember}/>
    </div>
  </div>
</Modal>

<Modal title={t.members.newMember} bind:show={addMember}>
  <ProjectMemberForm {projectId} {onSaved}/>
</Modal>
