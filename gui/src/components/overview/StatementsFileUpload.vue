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
                                 :state="!$v.selection.$invalid && !errorMessage"
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
        <b-row v-if="errorMessage">
            <b-col>
                <div class="error-text">
                    {{errorMessage}}
                </div>
            </b-col>
        </b-row>
    </b-card>
</template>

<script>
    import {required} from 'vuelidate/dist/validators.min'
    import {dateFormat, formatBytes} from "../../util/Formatters";
    import {api} from "../../api/RegularIncomeAPI";

    export default {
        name: "StatementsFileUpload",
        data() {
            return {
                selection: null,
                errorMessage: null,
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
                api.putFileToImport(this.selection)
                    .then(() => {
                        this.$emit('uploadSucceeded')

                        this.selection = null
                    })
                    .catch(e => {
                        // eslint-disable-next-line
                        console.log(e.response)

                        const error = e.response.data

                        this.errorMessage = `${error.path} \u2192 ${error.status} (${error.error})\n${error.message}`
                    })
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
    .error-text {
        background-color: wheat;
        color: red;
    }
</style>