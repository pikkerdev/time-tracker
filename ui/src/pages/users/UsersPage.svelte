<script  lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {t} from 'i18n'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {AuthRole, type User} from 'src/api/types'
  import SelectField from 'src/forms/SelectField.svelte'
  import {showToast} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'

  let users: User[] = []
  let authRoles = AuthRole

  const columns: [string, any][] = [
    [t.users.name, 'name'],
    [t.users.email, 'email'],
    [t.users.role, 'role'],
  ]

  async function save(user: User) {
    const newUser: User = await api.post(`users/${user.id}`, user)
    const userIndex = users.findIndex(u => u.id === newUser.id)
    users[userIndex] = newUser
    showToast(t.general.saved)
  }

  onMount(
    async () => users = await api.get('users')
    )
</script>

<MainPageLayout class="relative flex flex-col gap-4">
  <SortableTable class="w-1/2" {columns} items = {users} let:item>
    <tr>
      <td>{item.firstName} {item.lastName}</td>
      <td>{item.email}</td>
      <td><SelectField bind:value={item.authRole} options={authRoles}/></td>
      <td><Button onclick={() => save(item)} type="submit" label={t.general.save} class="primary"/></td>
    </tr>
  </SortableTable>
</MainPageLayout>
