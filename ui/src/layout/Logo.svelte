<script lang="ts">
  import {t} from 'i18n'
</script>

<img src="/favicon.svg" class="size-10" title="Time Tracker Logo" alt="Logo">
<div class="flex flex-col">
  <h1 class="font-bold text-2xl">
    {t.time}
    <span class="font-medium">{t.tracker}</span>
  </h1>
  <div class="bg-gray-200 h-1 rounded-2xl">
    <div class="bg-pikker-gold h-full w-2/5 rounded-2xl"></div>
  </div>
</div>
