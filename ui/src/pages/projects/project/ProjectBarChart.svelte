<script lang="ts">
  import SelectField from 'src/forms/SelectField.svelte'
  import type {LocalDate, MonthlyStats} from 'src/api/types'
  import {lang, t, toISODate} from 'i18n'

  const width = 600
  const height = 150
  const padding = 30
  const barWidth = (width - (2 * padding)) / 12
  const options = {
    last12: t.general.last12Months,
    ...Object.fromEntries(Array.from({ length: Math.max(1, new Date().getFullYear() - 2026 + 1) },
      (_, i) => [new Date().getFullYear() - i, new Date().getFullYear() - i]))
  }

  const modeOptions = {hours: t.invoices.hours, revenue: t.invoices.revenue}

  export let data: Record<LocalDate, MonthlyStats> = {};
  export let label: string|undefined = undefined

  let mode: 'hours' | 'revenue' = 'hours'
  let selectedRange: number | 'last12' = 'last12'
  let chartHeight = height - padding - padding;
  let months: LocalDate[] = []

  function formatDateMonths(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {year: 'numeric', month: 'short'})
  }

  function getMonths(range: number | 'last12') {
    if (range === 'last12') {
      months = Array.from({ length: 12 }, (_, i) => {
        return toISODate(new Date(), d => d.setFullYear(d.getFullYear(), d.getMonth() - 11 + i, 1));
      });
    } else {
      months = Array.from({ length: 12 }, (_, i) =>
        toISODate(new Date(), d => d.setFullYear(range, i, 1))
      );
    }
  }

  function getValue(stats: MonthlyStats | undefined, currentMode: 'hours' | 'revenue'): number {
    if (stats === undefined) return 0;
    if (currentMode === 'hours') return stats.billedHours + stats.unbilledHours
    else return stats.billedRevenue + stats.unbilledRevenue

  }

  $: getMonths(selectedRange)
  $: maxValue = Math.max(...months.map(m => getValue(data[m], mode)), 1);
</script>

<div class="flex flex-col gap-y-3">
  <div class="relative flex items-center justify-between px-10 gap-x-2">
    <span class="text-xl">{label ?? ''}</span>
    <div class="ml-auto flex items-center gap-x-2">
      <SelectField bind:value={mode} options={modeOptions} />
      <SelectField bind:value={selectedRange} {options} />
    </div>
  </div>

  <svg style="width: 100%; height: auto" viewBox="0 0 {width} {height}">
    <g transform="translate({padding}, {padding})">
      {#each months as month, i}
        {@const val = getValue(data[month], mode)}
        {@const barHeight = (val / maxValue) * chartHeight}
        {@const x = i * barWidth}
        {@const computedWidth = barWidth - 8}
        {@const y = chartHeight - barHeight}

        <rect {x} {y} width={computedWidth} height={barHeight} rx="2" class="fill-blue-700"/>

        <text font-size="10" x={x + computedWidth / 2} y={y - 6} text-anchor="middle">
          {val}
        </text>

        <text font-size="10" x={x + computedWidth / 2} y={chartHeight + 20} text-anchor="middle">
          {formatDateMonths(month)}
        </text>
      {/each}
    </g>
  </svg>
</div>
