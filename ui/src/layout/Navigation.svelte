<script lang="ts">
  import {user} from 'src/stores/auth'
</script>

{#if $user}
  <a href="/">Home</a>
  <a href="/projects">Projects</a>
    {#if $user.authRole === 'ADMIN'}
      <a href="/customers">Customers</a>
      <a href="/users">Users</a>
    {/if}
{/if}
