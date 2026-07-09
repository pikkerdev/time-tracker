<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  export let label: string|undefined = undefined
  export let activity: string|undefined
  export let projectActivities: string[]
  export let required = true

  let focused = false

</script>
<FormField {label} {required} {...$$restProps} >
  <input bind:value={activity} onfocus={() => focused = true} onblur={() => focused = false}
         {required} list="activities">
  {#if focused}
    <datalist id="activities">
      {#each projectActivities as t}
        <option>{t}</option>
      {/each}
    </datalist>
  {/if}
</FormField>
