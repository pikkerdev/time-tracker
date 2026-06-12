<script lang="ts">
  import type {LocalDate} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import {onMount} from 'svelte'
  import FormField from 'src/forms/FormField.svelte'
  import {lang, today} from 'i18n'

  export let dates: LocalDate[]
  export let timeEntryHours: Record<LocalDate, number>
  export let date = new Date().toISOString().slice(0, 10) as LocalDate

  function formatShortDate(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {day: 'numeric', month: 'short', weekday: 'short'})
  }

  let container: HTMLElement
  onMount(() => {
    container.scrollTo({left: container.scrollWidth})
  })
</script>

<div bind:this={container} class="overflow-x-scroll max-w-full flex gap-2 items-center p-2 pb-4">
  <FormField type="date" bind:value={date} max={today}/>
  {#each dates ?? [] as d}
    {@const percentage = timeEntryHours[d] * 100 / 8}
    <Button onclick={() => date = d}
            class="default px-1.5! py-1! flex-col {d == date ? 'ring-2 ring-primary-500' : ''}"
            style="background: linear-gradient(to top, #D7A262 0%, #D7A262 {percentage}%, white {percentage}%);">
      <div class="whitespace-pre-line text-xs!" class:text-muted={[0, 6].includes(new Date(d).getDay())}>{formatShortDate(d).replace(', ', '\n').replaceAll(' ', '\u00A0')}</div>
      <div class="text-block">{timeEntryHours[d] ?? 0}h</div>
    </Button>
  {/each}
</div>
