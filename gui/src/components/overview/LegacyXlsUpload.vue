<template>
    <div>
        <b-button class="mb-2"
                  v-b-modal="'statement-file-upload-modal'"
                  variant="primary">
            {{ true ? 'Legacy Upload' : 'Abort Upload' }}
        </b-button>

        <b-modal id="statement-file-upload-modal"
                 ref="statement-file-upload-modal"
                 title="Legacy XLS Import">

            <template v-slot:modal-footer>
                <b-button variant="secondary"
                          @click="close">
                    Abbrechen
                </b-button>

                <b-button variant="primary"
                          @click="upload"
                          :disabled="$v.$invalid">
                    Import
                </b-button>

            </template>

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

            <div v-if="errorMessage"
                 class="error-text">
                {{ errorMessage }}
            </div>
        </b-modal>
    </div>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {dateFormat, formatBytes} from "../../util/Formatters";
import {api} from "../../api/RegularIncomeAPI";

export default {
    name: "LegacyXlsUpload",
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
        close() {
            this.selection = null
            this.errorMessage = null

            this.$refs['statement-file-upload-modal'].hide()
        },
        upload() {
            api.getFileImports()
                .putFileToImport(this.selection)
                .then(() => {
                    this.$emit('uploadSucceeded')

                    this.close()
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
