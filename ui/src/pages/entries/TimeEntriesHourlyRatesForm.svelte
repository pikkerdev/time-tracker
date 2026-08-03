<script lang="ts">

  import type {Customer, Id, TimeEntry, TimeEntryView, UpdateHourlyRatesRequest} from 'src/api/types'
  import Form from 'src/forms/Form.svelte'
  import NumberField from 'src/forms/NumberField.svelte'
  import {t} from 'i18n'
  import api from 'src/api/api'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'

  export let selectedEntryIds: Id<TimeEntry>[] = []
  export let onSaved: (updatedEntries: TimeEntryView[]) => void = () => {}
  export let show: boolean

  let rate: number

  async function submit(){
    const updateHourlyRatesRequest: UpdateHourlyRatesRequest = {rate, timeEntryIds: selectedEntryIds}
    const updatedEntries: TimeEntryView[] = await api.post(`timeentries/hourlyRates`, updateHourlyRatesRequest)
    onSaved(updatedEntries)
    showToast(t.general.saved)
    show = false
  }

</script>

<Form {submit}>
  <NumberField label={t.invoices.rate} bind:value={rate} step="0.01"/>
  <Button class="primary" label={t.general.save} onclick={submit}/>
</Form>
