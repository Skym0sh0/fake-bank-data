<template>
    <div class="d-flex justify-content-between align-items-center" style="gap: 0.5em">
        <v-avatar color="warning" size="32">
            {{ avatar }}
        </v-avatar>

        <v-spacer/>

        <span>
            {{ username }}
        </span>

        <v-divider/>

        <v-btn :icon="true"
               :link="true"
               :small="true"
               to="/user-details">
            <v-icon>
                mdi-dots-vertical
            </v-icon>
        </v-btn>
    </div>
</template>

<script>
export default {
    name: "UserAvatar",
    computed: {
        username() {
            const fullname = [this.$root.userRef.user.firstname, this.$root.userRef.user.lastname].join(" ");
            if (fullname)
                return fullname;

            return this.$root.userRef.user.username;
        },
        avatar() {
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
