<script lang="ts">
  import {t} from 'i18n'
  import {AuthRole, type CountryCode, type User} from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'
  import Form from 'src/forms/Form.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import PhoneField from 'src/forms/PhoneField.svelte'

  const COUNTRY_CODE = "EE" as CountryCode

  export let user = {} as User
  export let onSaved: (user: User) => void = () => {}

  let authRoles = AuthRole

  async function submit() {
    user = await api.post(`users/${user.id}`, user)
    showToast(t.general.saved)
    onSaved(user)
  }
</script>

<Form {submit}>
  <PhoneField label={t.users.phone} bind:value={user.phone} countryCode={COUNTRY_CODE} required={false}/>
  <SelectField label={t.users.role} bind:value={user.authRole} options={authRoles}/>
  <Button type="submit" label={t.general.save} class="primary"/>
</Form>
