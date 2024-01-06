<template>
    <div class="subtitle-1">
        {{ username }}
    </div>
</template>

<script>
export default {
    name: "UserAvatar",
    computed: {
        username() {
            return this.$root.userRef.user.username;
        },
    },
}
</script>


<style scoped>

</style>