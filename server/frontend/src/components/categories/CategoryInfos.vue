<template>
    <div class="category-infos">
        <div v-show="entity.updatedAt" class="category-info">
            <div>Zuletzt geändert</div>
            <div>{{ formatDate(entity.updatedAt) }}</div>
        </div>
        <div v-show="entity.createdAt" class="category-info">
            <div>Erstellt am</div>
            <div>{{ formatDate(entity.createdAt) }}</div>
        </div>
        <div v-show="entity.usageCount" class="category-info">
            <div>Benutzt in</div>
            <div>{{ entity.usageCount }}</div>
        </div>
    </div>
</template>

<script>
import * as moment from "moment";

export default {
    name: "CategoryInfos",
    props: {
        entity: {
            type: Object,
            required: true,
        },
    },
    methods: {
        formatDate(date) {
            if (!date)
                return null
            return moment(date).format("YYYY-MM-DD HH:mm:ss")
        },
    },
}
</script>

<style scoped>
.category-infos {
    font-size: 10px;
    display: flex;
    flex-direction: column;
}

.category-infos > * {
    margin: 0;
}

.category-info {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}
</style>
