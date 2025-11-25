<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>
<template>
    <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/accounting">Accounting</RouterLink>
        <RouterLink to="/todo">Todo</RouterLink>
        <RouterLink to="/diet">Diet</RouterLink>
        <RouterLink to="/energy">Energy</RouterLink>
    </nav>

</template>

<style scoped>
.nav {
    display: flex;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background-color: white;
    border-bottom: 1px solid #eee;
}
</style>