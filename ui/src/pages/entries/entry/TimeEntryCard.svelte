<script lang="ts">
  import type {TimeEntryView} from 'src/api/types'
  import Icon from 'src/icons/Icon.svelte'

  export let entry: TimeEntryView
</script>

<div class="flex flex-col w-full bg-white px-4 py-2 rounded-lg border border-gray-300">
  <span class="font-semibold">
    {entry.entry.activity} - {entry.entry.hours}h
  </span>
  <p>{entry.entry.description}</p>
  <div class="flex items-center justify-between gap-4 ">
    <a href="/projects/{entry.entry.projectId}" class="flex items-center gap-1 text-gray-500 hover:text-pikker-gold transition-all duration-300">
      <Icon class="size-4.5!" name="code-folder"/>
      {entry.customerName} - {entry.projectName}
    </a>
    <div class="flex items-center gap-1">
      <slot/>
    </div>
  </div>
</div>
