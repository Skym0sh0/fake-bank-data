<template>
    <b-table :id="`statement-table-details-transations-table-${bankStatement.id}`"
             :fields="['date', 'amount', 'isPeriodic', 'reason']"
             :items="transactions">
    </b-table>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI";

    export default {
        name: "StatementTableDetails",
        props: {
            bankStatement: {
                required: true,
                type: Object,
            }
        },
        data() {
            return {
                transactions: [],
            }
        },
        methods: {
            loadTransactions() {
                api.getAllTransactions()
                    .then(res => {
                        this.transactions = res.data
                    })
            },
        },
        mounted() {
            this.loadTransactions()
        }
    }
</script>

<style scoped>

</style>