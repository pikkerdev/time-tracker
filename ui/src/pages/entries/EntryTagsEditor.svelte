<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  import {t} from 'i18n'

  export let label: string|undefined = undefined
  export let tag: string|undefined
  export let projectTags: string[]

  let focused = false

</script>
<FormField {label} {...$$restProps} required={false}>
  <input bind:value={tag} on:focus={() => focused = true} on:blur={() => focused = false}
         required={false} list="tags">
  {#if focused}
    <datalist id="tags">
      {#each projectTags as t}
        <option>{t}</option>
      {/each}
    </datalist>
  {/if}
</FormField>
