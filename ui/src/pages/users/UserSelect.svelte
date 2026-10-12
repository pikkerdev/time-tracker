<script lang="ts">
  import {t} from 'i18n'
  import SelectField from 'src/forms/SelectField.svelte'
  import type {Id, User} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'

  export let userId: Id<User> = ''

  let users: User[] = []

  onMount(async () => {
    users = await api.get('users')
  })
</script>

<SelectField bind:value={userId}
             emptyOption={t.timeEntries.allUsers}
             options={users.map(u => [u.id, u.name]).toObject()}/>
