<script lang="ts">
  import {user} from 'src/stores/auth'
  import {Link} from 'src/router'
</script>

<div class="text-lg flex items-center gap-6 max-md:hidden">
  {#if $user}
    <Link to="/projects">Projects</Link>
    {#if $user.isAdmin}
      <Link to="/customers">Customers</Link>
      <Link to="/users">Users</Link>
    {/if}
  {/if}
</div>
