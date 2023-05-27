<template>
    <b-btn id="statement-overview-import-new-statement"
           class="mb-2"
           variant="info"
           v-b-modal="'file-upload-modal'">
        Import CSV

        <b-modal id="file-upload-modal"
                 ref="file-upload-modal"
                 title="File Upload"
                 :centered="true"
                 header-bg-variant="warning"
                 footer-bg-variant="light">
            <template v-slot:modal-footer>
                <b-button variant="secondary"
                          @click="reset">
                    Abbrechen
                </b-button>
                <b-button variant="primary"
                          @click="doImportRequest"
                          :disabled="!fileSelection || !parsedPreview ||isUploading">
                    Import
                </b-button>
            </template>

            <b-form-group label-cols="2"
                          label-for="general-file-import-file"
                          label="Selected file:"
                          :horizontal="true">
                <b-form-file id="general-file-import-file"
                             v-model="fileSelection"
                             :disabled="isUploading"
                             :multiple="false"
                             :state="!$v.fileSelection.$invalid"
                             placeholder="Select file to import"
                             drop-placeholder="Drop file here to import"
                             accept=".csv"/>
            </b-form-group>

            <b-spinner v-if="isUploading"/>
            <template v-else>
                <p v-if="fileSelection">
                    {{ fileSelection.name }}
                </p>
                <p v-if="parsedPreview">
                    Preview:
                    {{ parsedPreview }}
                </p>
            </template>
        </b-modal>
    </b-btn>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {api} from "../../api/RegularIncomeAPI";

export default {
    name: "GeneralFileUpload",
    data() {
        return {
            isUploading: false,
            fileSelection: null,
            parsedPreview: null,
            errorMessage: null
        };
    },
    validations: {
        fileSelection: {
            required,
        },
    },
    methods: {
        doPreviewRequest() {
            this.isUploading = true;

            api.getFileImports()
                .postCsvImportPreview(this.fileSelection)
                .then(preview => {
                    this.parsedPreview = preview;

                    this.isUploading = false;
                })
                .catch(e => {
                    this.errorMessage = e.response.data;
                    this.isUploading = false;
                })
        },
        doImportRequest() {
            if (this.$v.$invalid)
                return;

            this.isUploading = true;

            api.getFileImports()
                .postCsvImport(this.fileSelection)
                .then(() => {
                    this.$emit("uploadSucceeded")
                    this.$refs["file-upload-modal"].hide();
                    this.reset();
                })
                .catch(e => {
                    this.errorMessage = e.response.data;
                    this.isUploading = false;
                })
        },
        reset() {
            this.fileSelection = null;
            this.parsedPreview = null;
            this.errorMessage = null;
            this.isUploading = false;
            this.$refs["file-upload-modal"].hide();
        },
    },
    watch: {
        fileSelection(newFile/*, oldFile*/) {
            if (newFile) {
                this.doPreviewRequest();
            }
        },
    }
}
</script>

<style scoped>

</style>