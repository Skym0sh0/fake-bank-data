<template>
    <b-btn id="statement-overview-import-new-statement"
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
                 @hide="checkToHide"
                 body-class="p-0">
            <template v-slot:modal-footer>
                <div class="w-100" style="display: flex;">
                    <b-container class="p-0">
                        <b-row>
                            <b-col class="p-0 px-2">
                                <h6>Importierbar</h6>
                                <b-progress :max="rawRows.length" :show-value="true">
                                    <b-progress-bar :value="importableRows.length" variant="success"/>
                                    <b-progress-bar :value="rawRows.length - importableRows.length"
                                                    variant="secondary"/>
                                </b-progress>
                            </b-col>

                            <b-col class="p-0 px-2">
                                <h6>Todos</h6>
                                <b-progress :max="importableRows.length" :show-value="true">
                                    <b-progress-bar :value="importableRows.length - rowsTodo.length" variant="success"/>
                                    <b-progress-bar :value="rowsTodo.length" variant="danger"/>
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

                <b-alert :show="!!errorMessage" variant="danger" :dismissible.camel="true">
                    {{ errorMessage }}
                </b-alert>
            </template>

            <waiting-indicator :is-loading="isUploading"/>

            <div v-if="!fileSelection">
                <b-container>
                    <v-row>
                        <v-col :cols="4">
                            <b-form-select v-model="selectedFileType"
                                           :options="supportedFileTypes"/>
                        </v-col>
                        <v-col>
                            <b-form-file id="general-file-import-file"
                                         v-model="fileSelection"
                                         :disabled="isUploading"
                                         :multiple="false"
                                         :state="!$v.fileSelection.$invalid"
                                         placeholder="Select file to import"
                                         drop-placeholder="Drop file here to import"
                                         accept=".csv"/>
                        </v-col>
                    </v-row>
                </b-container>
            </div>

            <template v-else>
                <b-card v-if="isReadilyLoaded" id="preview-card"
                        body-class="p-2">
                    <b-card-header class="d-flex justify-content-between py-2" id="preview-card-header">
                        <h6>{{ fileSelection.name }}</h6>
                        <h6>{{ selectedFileType }}</h6>
                    </b-card-header>

                    <b-card-body id="preview-card-body" class="p-2">
                        <turnover-preview-table v-model="previewedData"
                                                :categories="categories"
                                                @onCreateCategory="onCreateCategory"/>
                    </b-card-body>
                </b-card>
            </template>
        </b-modal>
    </b-btn>
</template>

<script>
import {required} from 'vuelidate/dist/validators.min'
import {api} from "../../api/RegularIncomeAPI";
import TurnoverPreviewTable from "@/components/turnovers/TurnoverPreviewTable.vue";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import {normalizeCategory} from "@/util/Normalizer";


export default {
    name: "TurnoverImporting",
    components: {
        WaitingIndicator,
        TurnoverPreviewTable
    },
    data() {
        return {
            isUploading: false,
            categories: null,
            supportedFileTypes: [],
            selectedFileType: null,
            fileSelection: null,
            parsedPreview: null,
            previewedData: null,
            errorMessage: null
        };
    },
    validations: {
        selectedFileType: {
            required,
        },
        fileSelection: {
            required,
        },
    },
    computed: {
        isImportImpossible() {
            const isDataValid = (this.previewedData || []).every(row => {
                if (!row.importable)
                    return true;

                return !!row.categoryId;
            });

            return this.$v.$invalid || !isDataValid || this.isUploading || this.importableRows.length <= 0;
        },
        rawRows() {
            return (this.previewedData || []);
        },
        importableRows() {
            return this.rawRows.filter(r => r.importable);
        },
        rowsTodo() {
            return this.importableRows.filter(r => !r.categoryId);
        },
        isReadilyLoaded() {
            return this.fileSelection && this.previewedData && this.categories
        },
    },
    methods: {
        loadCategories() {
            return api.getCategories()
                .fetchCategoryTree()
                .then(res => {
                    this.categories = res
                })
        },
        doPreviewRequest() {
            return api.getTurnovers()
                .previewTurnoverImport(this.selectedFileType, this.fileSelection)
                .then(preview => {
                    this.parsedPreview = preview;
                    this.previewedData = (this.parsedPreview.rows || [])
                        .map(row => ({
                            ...row,
                            originalImportable: row.importable
                        }));
                })
        },
        doImportRequest() {
            if (this.$v.$invalid)
                return;

            this.isUploading = true;

            api.getTurnovers()
                .createTurnoverImport(this.fileSelection, this.selectedFileType, this.importableRows)
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
        onCreateCategory({categoryName, callback}) {
            const normalized = normalizeCategory({
                name: categoryName,
            });

            this.isUploading = true;

            return api.getCategories()
                .postCategory(normalized)
                .then(() => this.loadCategories())
                .then(() => {
                    if (callback) {
                        callback()
                    }
                })
                .finally(() => this.isUploading = false)
        },
        reset() {
            this.categories = null;
            this.fileSelection = null;
            this.parsedPreview = null;
            this.previewedData = null;
            this.errorMessage = null;
            this.isUploading = false;
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
                this.isUploading = true;

                Promise.all([this.loadCategories(), this.doPreviewRequest()])
                    .catch(e => {
                        this.errorMessage = e.response.data;
                    })
                    .finally(() => this.isUploading = false)
            }
        },
    },
    mounted() {
        this.isUploading = true;

        api.getTurnovers()
            .getSupportedPreviewFormats()
            .then(res => {
                this.supportedFileTypes = res
                this.selectedFileType = this.supportedFileTypes[0] || null
            })
            .finally(() => this.isUploading = false)
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