<script lang="ts">
  import {t} from 'i18n'
  import Form from 'src/forms/Form.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import Button from 'src/components/Button.svelte'
  import {
    type Id,
    type Project,
    type ProjectMemberRequest,
    ProjectMemberRole,
    type ProjectMemberUser
  } from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'

  export let projectId: Id<Project>
  export let member = {} as ProjectMemberRequest
  export let onSaved: (member: ProjectMemberUser) => void


  async function submit() {
    onSaved(await api.post<ProjectMemberUser>(`projects/${projectId}/members`, member))
    showToast(t.general.saved)
  }
</script>

<Form {submit}>
  <FormField label={t.users.email} bind:value={member.email} type="email" autofocus/>
  <FormField label={t.users.firstName} required={false} bind:value={member.firstName}/>
  <FormField label={t.users.lastName} required={false} bind:value={member.lastName}/>
  <SelectField label={t.users.role} bind:value={member.role} options={ProjectMemberRole}/>
  <Button type="submit" label={t.general.save}/>
</Form>
