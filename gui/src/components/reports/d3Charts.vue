<template>
    <div>
        <div :id="target" class="border"/>
    </div>
</template>

<script>
    import * as d3 from "d3";

    export default {
        name: "d3Charts",
        props: {
            records: {
                type: Array,
            },
        },
        data() {
            return {
                target: `chart-spike-target-div-${Math.round(Math.random() * 1000000)}`,
                width: 1000,
                height: 600,
            }
        },
        watch: {
            records() {
                this.draw()
            },
        },
        methods: {
            draw() {
                if (!this.records)
                    return

                const vm = this
                const data = vm.records.map(rec => ({
                    date: new Date(rec.date),
                    amount: rec.balanceInCents,
                }))

                const xAxis = d3.scaleBand()
                    .domain(data.map(val => val.date))
                    .range([0, vm.width])
                    .paddingInner(0.25)
                    .paddingOuter(2)

                const yAxis = d3.scaleLinear()
                    .domain([d3.min(data, val => val.amount), d3.max(data, val => val.amount)])
                    .range([0, vm.height])

                const svg = d3.select(`#${vm.target}`)
                    .append('svg')
                    .attr('width', vm.width)
                    .attr('height', vm.height)
                    .attr('class', 'chart')

                const bars = svg.selectAll('record')
                    .data(data)
                    .enter()
                    .append('g')

                bars.append('rect')
                    .attr('class', 'record')
                    .attr('x', val => xAxis(val.date))
                    .attr('width', xAxis.bandwidth())
                    .attr('y', val => vm.height - yAxis(val.amount))
                    .attr('height', val => yAxis(val.amount))

                bars.append('g')
                    .attr('x', 10)
                    .attr('transform', 'rotate(0 0 0)')
                    .append('text')
                    .attr('x', val => xAxis(val.date))
                    .attr('y', val => vm.height - yAxis(val.amount))
                    .attr('dx', '.1em')
                    .attr('dy', '.1em')
                    .text('xXx')
            },
        },
    }
</script>

<style scoped>
</style>
