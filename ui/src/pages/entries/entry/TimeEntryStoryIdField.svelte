<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  import Icon from 'src/icons/Icon.svelte'

  export let label: string | undefined
  export let storyIds: string[] = []
  export let id: string|undefined = undefined
  export let required = true

  let value = ''

  function remove(i: number) {
    storyIds.splice(i, 1)
    storyIds = storyIds
  }

  function add(newValue: string) {
    storyIds.push(newValue)
    storyIds = storyIds
  }

  function handleInput(e: Event) {
    if (!storyIds) storyIds = []
    const target = e.target as HTMLInputElement
    value = target.value.replace(/\D/g, '')

    if (value.length === 9 && !storyIds.includes(value)) {
      add(value)
      value = ''
    }
  }
</script>

<FormField {label} bind:id {required} >
  <input {id} {required} type="text" maxlength="9" bind:value on:input={handleInput} {...$$restProps}/>
</FormField>
<div class="flex flex-wrap gap-1.5">
  {#each storyIds as s, i}
      <div class="bg-blue-200 rounded-md px-1 flex items-center gap-1">
        <Icon name="storytracker"/> {s} <button type="button" class="ml-1 py-0.5" on:click={() => remove(i)}>✕</button>
      </div>
    {/each}
</div>
