<script lang="ts">
  import {lang, t} from 'i18n'
  import SelectField from './SelectField.svelte'

  export let value = ''
  export let numberOfMonths = 12

  $: monthOptions = Object.fromEntries(
    Array.from({ length: numberOfMonths })
      .map((_, i) => {
        const d = new Date()
        d.setMonth(d.getMonth() - i)
        const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
        const label = d.toLocaleString(lang, {month: 'long', year: 'numeric'})
        return [key, label]
      })
  )
</script>

<SelectField
  title={t.timeEntries.chooseMonth}
  bind:value
  options={monthOptions}
  {...$$restProps}
/>
