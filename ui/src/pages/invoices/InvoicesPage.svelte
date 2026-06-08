<script lang="ts">
  import {t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import Button from 'src/components/Button.svelte'
  import {navigate} from 'src/router'
</script>

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <div slot="title">
    <Button onclick={() => navigate('/invoices/create')}>{t.invoices.createInvoice}</Button>
  </div>
</MainPageLayout>

