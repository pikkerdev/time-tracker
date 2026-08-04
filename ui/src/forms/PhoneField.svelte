<script lang="ts">
  import FormField from './FormField.svelte'
  import {countries} from 'i18n'
  import type {CountryCode} from 'src/api/types'

  export let label: string|undefined = undefined
  export let value: string = ''
  export let required = true
  export let countryCode: CountryCode|undefined = undefined

  $: areaCode = countries[countryCode as CountryCode]?.phoneAreaCode
  $: if (areaCode && value?.startsWith(areaCode)) value = '+' + value
  $: areaPrefix = areaCode ? '+' + areaCode : ''

  async function focus(e: Event) {
    if (!value) {
      value = areaPrefix
      const input = e.currentTarget as HTMLInputElement
      setTimeout(() => input?.setSelectionRange(value.length, value.length))
    }
  }

  function blur() {
    if (value == areaPrefix) value = ''
  }

  function paste(e: ClipboardEvent) {
    e.preventDefault()
    const pasted = (e.clipboardData || e.clipboardData)?.getData('text')
    value = (pasted?.startsWith("+") ? '' : areaPrefix) + pasted
  }
</script>

<svelte:options accessors/>

<FormField type="tel" bind:value {label} onfocus={focus} onblur={blur} onpaste={paste} minlength={10} maxlength={15} {required} {...$$restProps}/>
