<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {t} from 'i18n'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import { type User} from 'src/api/types'
  import {showToast, ToastType} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'
  import Modal from 'src/components/Modal.svelte'
  import UserForm from 'src/pages/users/UserForm.svelte'

  let users: User[] = []
  let edit: User|false = false

  async function save(user: User) {
    try {
      const newUser: User = await api.post(`users/${user.id}`, user)
      const userIndex = users.findIndex(u => u.id === newUser.id)
      users[userIndex] = newUser
      showToast(t.general.saved)
    } catch (e) {
      users = await api.get('users')
      showToast(t.errors.cannotModifyInitialAdminRole, { type: ToastType.ERROR })
    }
  }

  function onSaved(user: User) {
    users = users.replaceById(user)
    edit = false
  }

  onMount(
    async () => users = await api.get('users')
  )
</script>

<MainPageLayout class="relative flex flex-col gap-4" title={t.users.title}>
  <SortableTable columns={[
    'name',
    'email',
    'phone',
    [t.users.role, u => u.authRole],
    ['', '']
  ]} items={users} let:item>
    <tr>
      <td>{item.name}</td>
      <td>{item.email}</td>
      <td>{item.phone}</td>
      <td>{item.authRole}</td>
      <td>
        <Button label={t.general.edit} onclick={() => edit = structuredClone(item)}/>
      </td>
    </tr>
  </SortableTable>
</MainPageLayout>

<Modal title={t.users.user} bind:show={edit}>
  {#if edit}
    <UserForm user={edit} {onSaved}/>
  {/if}
</Modal>
