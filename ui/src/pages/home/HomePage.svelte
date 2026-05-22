<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t} from 'i18n'
</script>

<MainPageLayout class="flex flex-col gap-4 lg:gap-8">
  <div class="flex flex-col items-center text-center">
    <h4 class="text-lg lg:text-2xl">{t.home.intro}.</h4>
    <h5 class="text-base lg:text-lg">{t.home.slogan}!</h5>
  </div>
</MainPageLayout>
