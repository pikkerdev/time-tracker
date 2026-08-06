<script lang="ts">
  import SelectField from 'src/forms/SelectField.svelte'
  import type {LocalDate} from 'src/api/types'
  import {lang, toISODate} from 'i18n'

  export let data: Record<LocalDate, number> = {};
  export let label: string|undefined = undefined

  const width = 600
  const height = 150
  const padding = 30
  const barWidth = (width- (2*padding)) / 12
  const options = {last12: 'Last 12 Months',
    ...Object.fromEntries(Array.from({ length: Math.max(1, new Date().getFullYear() - 2020 + 1) },
        (_, i) => [new Date().getFullYear() - i, new Date().getFullYear() - i]))
  }

  let selectedRange: number | 'last12' = 'last12'
  let chartHeight = height - padding - padding;
  let months: LocalDate[] = []

  function formatDate(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {year: 'numeric', month: 'short'})
  }

  function getMonths(range: number | 'last12') {
    if (range === 'last12') {
      months = Array.from({ length: 12 }, (_, i) => {
        return toISODate(new Date(), d => d.setFullYear(d.getFullYear(), d.getMonth() - 11 + i, 1));
      });
    } else {
      months = Array.from({ length: 12 }, (_, i) =>
        toISODate(new Date(), d =>  d.setFullYear(range, i, 1))
      );
    }
  }

  $: maxValue = Math.max(...Object.values(data), 1);
  $: getMonths(selectedRange)

</script>

<div class="flex flex-col gap-y-3">
  <div class="relative flex items-center justify-center">
    <span class="text-xl">{label}</span>
    <SelectField class="absolute right-0" bind:value={selectedRange} {options}/>
  </div>
  <svg style="width: 100% ;height: auto" viewBox="0 0 {width} {height}">
    <g transform="translate({padding}, {padding})">
      {#each months as month, i}
        {@const hours = data[month] ?? 0}
        {@const barHeight = (hours / maxValue) * chartHeight}
        {@const x = i * barWidth}
        {@const computedWidth = barWidth - 8}
        {@const y = chartHeight - barHeight}
        <rect {x} {y} width={computedWidth} height={barHeight} rx="2" class="fill-blue-700"/>
        <text class="text-xs" x={x + computedWidth / 2} y={y - 6} text-anchor="middle">
          {hours}
        </text>
        <text font-size="10" x={x + computedWidth / 2} y={chartHeight + 20} text-anchor="middle">
          {formatDate(month)}
        </text>
      {/each}
    </g>
  </svg>
</div>
