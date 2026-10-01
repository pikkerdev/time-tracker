<script lang="ts">
  import {lang, toISODate} from 'i18n'
  import SelectField from './SelectField.svelte'

  export let numberOfMonths = 12
  export let from: string | undefined
  export let to: string | undefined

  let value = ''

  const d = new Date()
  d.setDate(1)
  const monthOptions = Object.fromEntries(
    Array.from({length: numberOfMonths})
      .map(() => {
        const key = toISODate(d) + ':' + toISODate(new Date(d.getFullYear(), d.getMonth() + 1, 0))
        const label = d.toLocaleString(lang, {month: 'long', year: 'numeric'})
        d.setMonth(d.getMonth() - 1)
        return [key, label]
      })
  )

  function onchange(e: FormEvent) {
   [from, to]  = e.currentTarget.value.split(':')
  }

  $: value = Object.keys(monthOptions).find(m => m.split(':')[0] === from) ?? ''
</script>

<SelectField {value} {onchange} options={monthOptions} {...$$restProps}/>
