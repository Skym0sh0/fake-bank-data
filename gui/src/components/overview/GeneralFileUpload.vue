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
                 :scrollable="true"
                 header-bg-variant="warning"
                 footer-bg-variant="light"
                 @hidden="reset">
            <template v-slot:modal-footer>
                <b-button variant="secondary"
                          @click="reset">
                    Abbrechen
                </b-button>
                <b-button variant="primary"
                          @click="doImportRequest"
                          :disabled="!fileSelection || !parsedPreview || !previewedData || isUploading">
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
                <p v-if="previewedData">
                    <preview-data v-model="previewedData"/>
                </p>
            </template>
        </b-modal>
    </b-btn>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {api} from "../../api/RegularIncomeAPI";
import PreviewData from "@/components/overview/PreviewData.vue";

export default {
    name: "GeneralFileUpload",
    components: {
        PreviewData
    },
    data() {
        return {
            isUploading: false,
            fileSelection: null,
            parsedPreview: null,
            previewedData: null,
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
                    this.previewedData = this.parsedPreview.rows.filter((x, idx) => idx <= 5);

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
                .postCsvImport(this.previewedData)
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
            this.previewedData = null;
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

<style>
@media (min-width: 800px) {
    .modal-md {
        max-width: 80% !important;
        /*width: 90% !important;*/
    }
}

@media (min-width: 576px) {
    .modal-dialog {
        max-width: 80% !important;
        width: 80% !important;
    }
}
</style>