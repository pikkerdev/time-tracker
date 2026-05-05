<script lang="ts">
  import {user} from 'src/stores/auth'
</script>

<div class="text-lg flex items-center gap-6 max-md:hidden">
  {#if $user}
    <a href="/projects">Projects</a>
    {#if $user.authRole === 'ADMIN'}
      <a href="/customers">Customers</a>
      <a href="/users">Users</a>
    {/if}
  {/if}
</div>
