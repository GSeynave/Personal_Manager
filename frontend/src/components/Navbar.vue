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
nav {
    display: flex;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background-color: var(--surface);
    border-bottom: 1px solid var(--accent);
    padding: 1rem 2rem;
    gap: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

nav a {
    text-decoration: none;
    color: var(--text);
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    transition: all 0.2s;
    font-weight: 500;
}

nav a:hover {
    background-color: var(--bg);
    color: var(--primary);
}

nav a.router-link-active {
    background-color: var(--primary);
    color: var(--surface);
}
</style>