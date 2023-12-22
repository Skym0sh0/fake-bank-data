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
                <div v-show="uploadingTime">
                    Time to parse for preview: {{ uploadingTime / 1000.0 }}s
                </div>

                <b-button variant="secondary"
                          @click="reset">
                    Abbrechen
                </b-button>
                <b-button variant="primary"
                          @click="doImportRequest"
                          :disabled="$v.$invalid || isUploading">
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
                <b-card v-show="fileSelection" :title="fileSelection.name">
                    <preview-data v-model="previewedData"/>
                </b-card>
            </template>
        </b-modal>
    </b-btn>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {api} from "../../api/RegularIncomeAPI";
import PreviewData from "@/components/turnovers/PreviewData.vue";

export default {
    name: "TurnoverImporting",
    components: {
        PreviewData
    },
    data() {
        return {
            isUploading: false,
            uploadingTime: null,
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
        previewedData: {
            $each: {
                categoryId: {
                    required,
                }
            }
        }
    },
    methods: {
        doPreviewRequest() {
            this.isUploading = true;
            this.uploadingTime = null;
            const startTime = new Date();

            api.getTurnovers()
                .previewTurnoverImport(this.fileSelection)
                .then(preview => {
                    this.parsedPreview = preview;
                    this.previewedData = this.parsedPreview.rows;
                })
                .catch(e => {
                    this.errorMessage = e.response.data;
                })
                .finally(() => {
                    this.isUploading = false;
                    this.uploadingTime = new Date().getTime() - startTime.getTime();
                })
        },
        doImportRequest() {
            if (this.$v.$invalid)
                return;

            this.isUploading = true;

            api.getTurnovers()
                .createTurnoverImport(this.fileSelection, this.previewedData)
                .then(() => {
                    this.$emit("uploadSucceeded")
                    this.$refs["file-upload-modal"].hide();
                    this.reset();
                })
                .catch(e => {
                    this.errorMessage = e.response.data;
                })
                .finally(() => {
                    this.isUploading = false;
                })
        },
        reset() {
            this.fileSelection = null;
            this.parsedPreview = null;
            this.previewedData = null;
            this.errorMessage = null;
            this.isUploading = false;
            this.uploadingTime = null;
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