<script lang="ts">
  import {t} from 'i18n'

  export let clientName: string
  export let clientAddress: string
  export let clientRegNo: string

</script>

<section class="receiver-info">
  <h2 class="text-lg font-bold">{t.customers.name}</h2>
  <p>{clientName}</p>
  <p>{clientAddress}</p>
  <p>{clientRegNo}</p>
</section>
