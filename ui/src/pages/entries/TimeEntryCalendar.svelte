<script lang="ts">
  import type {LocalDate} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import {onMount} from 'svelte'
  import FormField from 'src/forms/FormField.svelte'
  import {lang, today} from 'i18n'

  export let dates: LocalDate[]
  export let timeEntryHours: Record<LocalDate, Record<string, number>>
  export let date = new Date().toISOString().slice(0, 10) as LocalDate

  function formatShortDate(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {day: 'numeric', month: 'short', weekday: 'short'})
  }

  function totalHours(date: Record<string, number>):number{
    let fixedDate = date || {};
    return Object.values(fixedDate)?.reduce((sum, h) => sum + h, 0);
  }

  function getGradientStyle(date: Record<string, number>): string {
    let fixedDate = date || {};
    let total = 8
    if (totalHours(fixedDate) > 8) total = totalHours(fixedDate)

    let currentPercent = 0;
    const stops: string[] = [];

    for (const [color, hours] of Object.entries(fixedDate)) {
      if (hours > 0) {
        const start = currentPercent;
        const end = start + (hours / total) * 100;
        stops.push(`${color} ${start}%, ${color} ${end}%`);

        currentPercent = end;
      }
    }
    stops.push(`white ${currentPercent}%, white ${100}%`);
    return `background: linear-gradient(to top, ${stops.join(', ')});`;
  }

  let container: HTMLElement
  onMount(() => {
    container.scrollTo({left: container.scrollWidth})
  })

</script>

<div bind:this={container} class="overflow-x-scroll max-w-full flex gap-2 items-center p-2 pb-4">
  <FormField type="date" bind:value={date} max={today}/>
  {#each dates ?? [] as d}
    <Button onclick={() => date = d}
            class="default px-1.5! py-1! flex-col {d == date ? 'ring-2 ring-primary-500' : ''}"
            style={getGradientStyle(timeEntryHours[d])}>
      <div class="whitespace-pre-line text-xs!" class:text-muted={[0, 6].includes(new Date(d).getDay())}>{formatShortDate(d).replace(', ', '\n').replaceAll(' ', '\u00A0')}</div>
      <div class="text-block">{totalHours(timeEntryHours[d])}h</div>
    </Button>
  {/each}
</div>
