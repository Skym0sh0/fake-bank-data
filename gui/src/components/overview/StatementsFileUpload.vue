<template>
    <b-card>
        <b-row align-h="between">
            <b-col>
                <b-form-group label-cols="2"
                              label-for="file-import-selection"
                              label="Selected File:"
                              horizontal
                              :description="readFileInformation">
                    <b-form-file id="file-import-selection"
                                 v-model="selection"
                                 placeholder="Select file to import"
                                 drop-placeholder="Drop file here to import"
                                 :state="!$v.selection.$invalid"
                                 accept=".xls"/>
                </b-form-group>
            </b-col>

            <b-col cols="2">
                <b-button-group>
                    <b-button variant="primary"
                              @click="upload"
                              :disabled="$v.$invalid">
                        Import
                    </b-button>

                    <b-button variant="secondary"
                              @click="reset">
                        Reset
                    </b-button>
                </b-button-group>
            </b-col>
        </b-row>
    </b-card>
</template>

<script>
    import {required} from 'vuelidate/dist/validators.min'
    import {dateFormat, formatBytes} from "../../util/Formatters";

    export default {
        name: "StatementsFileUpload",
        data() {
            return {
                selection: null,
            }
        },
        validations: {
            selection: {
                required,
            },
        },
        methods: {
            reset() {
                this.selection = null
            },
            upload() {
                // eslint-disable-next-line
                console.log(this.selection)

                this.$emit('uploadSucceeded')
            },
        },
        computed: {
            readFileInformation() {
                if (!this.selection)
                    return null

                return `Size: '${formatBytes(this.selection.size)}' - Last modified: '${dateFormat.formatTimestamp(this.selection.lastModifiedDate)}'`
            },
        },
    }
</script>

<style scoped>

</style>