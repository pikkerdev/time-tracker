<script lang="ts">
  import type {LocalDate} from 'src/api/types'
  import Button from 'src/components/Button.svelte'

  export let dates: LocalDate[] = []
  export let timeEntryHours: Record<LocalDate, number> = {}
  export let chosenDate = new Date().toISOString().slice(0, 10) as LocalDate

</script>

<div class="flex flex-wrap h-fit gap-1 my-3 text-lg">
  {#each dates ?? [] as date}
    {@const percentage = timeEntryHours[date] * 100 / 8}
    <Button onclick={() => chosenDate = date}
            class="border border-gray-300 rounded-lg px-4 py-3 bg-white hover:bg-stone-50 size-20 overflow-hidden"
            style="background: linear-gradient(to top, #3b82f6 0%, #3b82f6 {percentage}%, #ffffff {percentage}%, #ffffff 100%);">
      {date}
      {timeEntryHours[date]}
    </Button>
  {/each}
</div>

