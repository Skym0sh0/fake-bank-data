<template>
    <div>
        blablaba

        <div :id="target"/>
    </div>
</template>

<script>
    import * as c3 from "c3";

    export default {
        name: "c3Charts",
        props: {
            records: {
                type: Array,
            }
        },
        data() {
            return {
                target: `chart-spike-target-div-${Math.round(Math.random() * 1000000)}`,
                width: 800,
                height: 600,
            }
        },
        methods: {
            draw() {
                const chart = c3.generate({
                    bindto: `#${this.target}`,
                    data: {
                        columns: [
                            ['data1', 30, 200, 100, 400, 150, 250],
                            ['data2', 50, 20, 10, 40, 15, 25],
                        ],
                    },
                })

                console.log(chart)
            },
        },
        mounted() {
            this.draw()
        },
    }
</script>

<style scoped>

</style>