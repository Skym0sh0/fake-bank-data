<template>
    <div class="subtitle-1 d-flex justify-content-center align-items-baseline border rounded px-1" style="gap: 0.25em">
        <v-avatar color="warning" size="24">
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
            return this.username.split(' ')
                .filter(word => word.length > 0)
                .map(word => word[0])
                .join("");
        },
    },
}
</script>


<style scoped>

</style>