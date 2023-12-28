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
                 @hidden="reset"
                 @hide="checkToHide">
            <template v-slot:modal-footer>
                <div class="w-100" style="display: flex;">
                    <b-container class="p-0">
                        <b-row>
                            <b-col class="p-0 px-2">
                                <h6>Importierbar</h6>
                                <b-progress :max="rawRows.length" :show-value="true">
                                    <b-progress-bar :value="importableRows.length" variant="success"/>
                                    <b-progress-bar :value="rawRows.length - importableRows.length" variant="warning"/>
                                </b-progress>
                            </b-col>

                            <b-col class="p-0 px-2">
                                <h6>Todos</h6>
                                <b-progress :max="rawRows.length" :show-value="true">
                                    <b-progress-bar :value="rawRows.length - rowsTodo.length" variant="success"/>
                                    <b-progress-bar :value="rowsTodo.length" variant="danger"/>
                                </b-progress>
                            </b-col>

                            <b-col class="p-0 px-2">
                                <h6>Time to parse for preview</h6>
                                <b-progress :max="uploadingTime/1000 * 1.3" :show-value="true" :precision="3">
                                    <b-progress-bar :value="uploadingTime/1000" variant="success"/>
                                </b-progress>
                            </b-col>
                        </b-row>
                    </b-container>

                    <div class="m-auto">
                        <b-button class="mr-1"
                                  variant="secondary"
                                  @click="reset">
                            Abbrechen
                        </b-button>
                        <b-button variant="primary"
                                  @click="doImportRequest"
                                  :disabled="isImportImpossible">
                            Import
                        </b-button>
                    </div>
                </div>

                <b-alert :show="errorMessage" variant="danger" :dismissible.camel="true">
                    {{ errorMessage }}
                </b-alert>
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
                <b-card v-if="fileSelection" :title="fileSelection.name">
                    <turnover-preview-table v-model="previewedData"/>
                </b-card>
            </template>
        </b-modal>
    </b-btn>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {api} from "../../api/RegularIncomeAPI";
import TurnoverPreviewTable from "@/components/turnovers/TurnoverPreviewTable.vue";

export default {
    name: "TurnoverImporting",
    components: {
        TurnoverPreviewTable
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
    computed: {
        isImportImpossible() {
            return this.$v.$invalid || this.isUploading || this.importableRows.length <= 0;
        },
        rawRows() {
            return (this.previewedData || []);
        },
        importableRows() {
            return this.rawRows.filter(r => r.importable);
        },
        rowsTodo() {
            return this.rawRows.filter(r => !r.categoryId);
        },
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
                .createTurnoverImport(this.fileSelection, this.importableRows)
                .then(() => {
                    this.$emit("uploadSucceeded")
                    this.$refs["file-upload-modal"].hide();
                    this.reset();
                })
                .catch(e => {
                    this.errorMessage = e.response.data.message;
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
        checkToHide(e) {
            const mustNotBeClosed = this.isUploading || (this.parsedPreview && !(
                e.trigger === null // abort button was pressed
                || e.trigger === 'headerclose' // X Button in header was pressed
            ));
            if (mustNotBeClosed) {
                e.preventDefault()
            }
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