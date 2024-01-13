<template>
    <b-container v-if="parsedData"
                 :fluid="true"
                 style="font-size: 0.75em; line-height: 1"
                 class="mb-2">
        <b-row :no-gutters="true">
            <b-col>
                <b-table-lite :striped="true"
                              :sticky-header="true"
                              :hover="true"
                              :small="true"
                              :responsive="true"
                              :bordered="true"
                              :items="dataRows"
                              style="min-height: 60vh; margin: 0"/>
            </b-col>
        </b-row>
        <b-row :no-gutters="true">
            <b-col>Bytes: {{ file.size }}</b-col>
            <b-col>Rows: {{ parsedData.rows }}</b-col>
            <b-col>Columns: {{ parsedData.columns }}</b-col>
        </b-row>
    </b-container>
</template>

<script>

import {api} from "@/api/RegularIncomeAPI";

export default {
    name: "RawCsvFileTable",
    props: {
        file: {
            type: File,
            required: true,
        },
        encoding: {
            type: String,
            required: false,
        },
    },
    data() {
        return {
            parsedData: null,
        };
    },
    methods: {
        triggerParsing() {
            this.parsedData = null;

            if (!this.file)
                return;

            api.getTurnovers()
                .rawCsvTablePreview(this.file, this.encoding)
                .then(data => {
                    return this.parsedData = data;
                })
        },
    },
    computed: {
        dataRows() {
            if (!this.parsedData)
                return [];

            return this.parsedData.data.map(row => {
                return row.map(cell => {
                    const maxLength = 24;
                    const trailer = "...";

                    if (!cell)
                        return null;

                    if (cell.length > maxLength)
                        return cell.substring(0, maxLength - trailer.length) + trailer;

                    return cell.substring(0, maxLength);
                })
            })
        },
    },
    watch: {
        file() {
            this.triggerParsing();
        },
        encoding() {
            this.triggerParsing();
        },
    },
    mounted() {
        this.triggerParsing();
    },
}
</script>


<style scoped>

</style>