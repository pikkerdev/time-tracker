<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  import {t} from 'i18n'

  export let label: string|undefined = undefined
  export let activity: string|undefined
  export let projectActivities: string[]

  let focused = false

</script>
<FormField {label} {...$$restProps} required={false}>
  <input bind:value={activity} on:focus={() => focused = true} on:blur={() => focused = false}
         required={false} list="activities">
  {#if focused}
    <datalist id="activities">
      {#each projectActivities as t}
        <option>{t}</option>
      {/each}
    </datalist>
  {/if}
</FormField>
