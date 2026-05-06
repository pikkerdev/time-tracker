<script  lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {t} from 'i18n'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import type {User} from 'src/api/types'
  import SelectField from 'src/forms/SelectField.svelte'

  let users: User[]
  let authRoles: { [value: string]: string } = {}

  const columns: [string, any][] = [
    [t.users.name, 'name'],
    [t.users.email, 'email'],
    [t.users.role, 'role'],
  ]

  onMount(
    async () => {
      users = await api.get('users')
      authRoles = await api.get('users/authroles')
    })

</script>

<MainPageLayout class="relative flex flex-col gap-4">
  <SortableTable class="w-1/2" {columns} items = {users} let:item>
    <tr>
      <td>{item.firstName} {item.lastName}</td>
      <td>{item.email}</td>
      <td><SelectField bind:value={item.authRole} options={authRoles}/></td>
    </tr>
  </SortableTable>
</MainPageLayout>
