<script lang="ts">
  import {lang, toISODate} from 'i18n'
  import SelectField from './SelectField.svelte'

  export let numberOfMonths = 12
  export let from: string
  export let to: string

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
  let value = Object.keys(monthOptions)[0]
  valueChanged()

  function onchange(e: FormEvent) {
    value = e.currentTarget.value
    valueChanged()
  }

  function valueChanged() {
    [from, to] = value.split(':')
  }

  $: if (value != from + ':' + to) value = ''
</script>

<SelectField {value} {onchange} options={monthOptions} {...$$restProps}/>
