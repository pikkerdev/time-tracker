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

  let users: User[]
  let authRoles = AuthRole
  let user = {} as User

  const columns: [string, any][] = [
    [t.users.name, 'name'],
    [t.users.email, 'email'],
    [t.users.role, 'role'],
  ]

  async function onClick() {
    api.post('users', user)
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
      <td> <Button {onClick} type="submit" label={t.general.save} class="primary"/></td>
    </tr>
  </SortableTable>
</MainPageLayout>
