<template>
    <div class="subtitle-1 d-flex justify-content-center align-items-baseline border rounded px-1" style="gap: 0.25em">
        <v-avatar color="warning" size="32">
            {{ avatar }}
        </v-avatar>
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
        avatar() {
            console.log(this.username.split(/\s+/)
                .filter(word => word.length > 0)
                .map(word => word.substring(0, 1)))

            return this.username.split(/\s+/)
                .filter(word => word.length > 0)
                .map(word => word.substring(0, 1))
                .filter((letter, idx) => idx < 2)
                .join("")
                .toUpperCase();
        },
    },
}
</script>


<style scoped>

</style>