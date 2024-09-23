<script setup lang="ts">
import {computed, inject, onMounted, ref} from "vue";
import {
  Category,
  CategoryApi,
  CategoryPatch,
  TurnoverImportFormat,
  TurnoverImportPatch,
  TurnoverPreview,
  TurnoverRowPreview,
  TurnoversApi
} from "@api/api.ts";
import {apiRefKey} from "../../../keys.ts";
import WaitingIndicator from "../../misc/WaitingIndicator.vue";
import useVuelidate from "@vuelidate/core";
import {required} from '@vuelidate/validators'
import RawCsvFileTable from "./RawCsvFileTable.vue";
import TurnoverPreviewTable from "./TurnoverPreviewTable.vue";

function getBankFormatName(frmt: TurnoverImportFormat): string {
  const BANK_FORMAT_NAMES = {
    "VR_BANK_CSV": "CSV VR-Bank",
    "DKB": "CSV DKB",
    "NEW_DKB": "CSV DKB (neue GUI)",
    "PAYPAL": "CSV PayPal"
  };

  return BANK_FORMAT_NAMES[frmt] || frmt;
}

type SupportedFileType = {
  value: TurnoverImportFormat | null;
  title: string;
};

export type PreviewRowWithOriginalState = TurnoverRowPreview & { originalImportable?: boolean };

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;
const categoriesApi: CategoryApi | undefined = inject(apiRefKey)?.categoriesApi;

const {isLoading} = defineProps<{
  isLoading?: boolean;
}>();

const emit = defineEmits<{
  (e: 'uploadSucceeded'): void;
}>();

const isDialogOpen = ref<boolean>(false);

const isUploading = ref<boolean>(false);
const categories = ref<Category[] | null>(null);

const supportedFileTypes = ref<SupportedFileType[]>([]);
const supportedFileEncodings = ref(["UTF-8", "Windows-1250", "UTF-16", "UTF-32"]);

const selectedFileType = ref<TurnoverImportFormat | null>(null);
const selectedFileEncoding = ref<string>(supportedFileEncodings.value[0]/*"UTF-8"*/);
const fileSelection = ref<File | null>(null);

const v$ = useVuelidate({
  selectedFileType: {required},
  selectedFileEncoding: {required},
  fileSelection: {required},
}, {
  selectedFileType,
  selectedFileEncoding,
  fileSelection
});

const parsedPreview = ref<TurnoverPreview | null>(null);
const previewedData = ref<PreviewRowWithOriginalState[] | null>(null);
const errorMessage = ref<string | null>(null);

const isImportImpossible = computed(() => {
  const isDataValid = (previewedData.value || []).every(row => {
    if (!row.importable)
      return true;

    return !!row.categoryId;
  });

  return /*this.$v.$invalid ||*/ !isDataValid || isUploading.value || importableRows.value.length <= 0;
})

const rawRows = computed(() => {
  return (previewedData.value || []);
})

const importableRows = computed(() => {
  return rawRows.value.filter(r => r.importable);
})

const rowsTodo = computed(() => {
  return importableRows.value.filter(r => !r.categoryId);
})

const isReadilyLoaded = computed(() => {
  return fileSelection.value && previewedData.value && categories.value
})

function loadCategories() {
  return categoriesApi?.getCategoriesAsTree()
    .then((res: Category[]) => {
      categories.value = res;
    })
}

function onIsLoading(isLoading: boolean) {
  isUploading.value = isLoading
}

function onStartPreview() {
  isUploading.value = true;

  Promise.all([loadCategories(), doPreviewRequest()])
    .catch(e => {
      errorMessage.value = e.response.data;
    })
    .finally(() => isUploading.value = false)
}

function doPreviewRequest() {
  return api?.processPreview(selectedFileType.value!, fileSelection.value!, selectedFileEncoding.value)
    .then((preview: TurnoverPreview) => {
      parsedPreview.value = preview;

      previewedData.value = (parsedPreview.value.rows || [])
        .map((row: TurnoverRowPreview) => ({
          ...row,
          originalImportable: row.importable
        }))
    })
}

function doImportRequest() {
  if (v$.value.$invalid)
    return;

  isUploading.value = true;

  const patch: TurnoverImportPatch = {
    encoding: selectedFileEncoding.value,
    format: selectedFileType.value!,
    rows: importableRows.value,
  }

  api?.createTurnoverImport(fileSelection.value!, patch)
    .then(() => {
      emit("uploadSucceeded")
      // this.$refs["file-upload-modal"].hide();
      reset();
    })
    .catch(e => {
      errorMessage.value = e.response.data.message;
    })
    .finally(() => isUploading.value = false)
}

function onCreateCategory({categoryName, callback}: { categoryName: string; callback?: () => void; }) {
  const normalized: CategoryPatch = {
    name: categoryName,
  };

  isUploading.value = true;

  return categoriesApi?.createCategory(normalized)
    .then(() => loadCategories())
    .then(() => {
      callback?.()
    })
    .finally(() => isUploading.value = false)
}

function reset() {
  isDialogOpen.value = false;

  categories.value = null;
  // this.selectedFileType = null;
  // this.selectedFileEncoding = "UTF-8";
  fileSelection.value = null;
  parsedPreview.value = null;
  previewedData.value = null;
  errorMessage.value = null;
  isUploading.value = false;
  // this.$refs["file-upload-modal"].hide();
}

function checkToHide(e) {
  const mustNotBeClosed = isUploading.value || (parsedPreview.value && !(
    e.trigger === null // abort button was pressed
    || e.trigger === 'headerclose' // X Button in header was pressed
  ));
  if (mustNotBeClosed) {
    e.preventDefault()
  }
}

function openDialog() {
  reset();
  isDialogOpen.value = true
}

onMounted(() => {
  isUploading.value = true;

  api?.getSupportedFormats()
    .then((formats: TurnoverImportFormat[]) => {
      supportedFileTypes.value = [
        {
          value: null,
          title: "Dateiformat wählen",
        },
        ...formats.map(f => ({
          value: f,
          title: getBankFormatName(f)
        }))
      ];
    })
    .finally(() => isUploading.value = false)
})
</script>

<template>
  <v-dialog v-model="isDialogOpen"
            :persistent="true"
            @update:model-value="reset">
    <template v-slot:activator>
      <v-btn color="primary"
             :disabled="isLoading"
             :loading="isLoading"
             @click="openDialog"
             variant="elevated"
             text="CSV Import"
             prepend-icon="mdi-earth"/>
    </template>

    <template v-slot:default>
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          <span>Importiere CSV Datei</span>

          <v-btn @click="reset"
                 variant="plain"
                 icon="mdi-close"/>
        </v-card-title>

        <v-card-text>
          <waiting-indicator :is-loading="isUploading"/>

          <div v-if="!parsedPreview">
            <v-container>
              <v-row align="center">
                <v-col :sm="6" :md="7">
                  <v-file-input id="general-file-import-file"
                                v-model="fileSelection"
                                :disabled="isUploading"
                                :multiple="false"
                                accept="text/csv"
                                :show-size="1024"
                                :counter="true"
                                label="Import CSV Datei"
                                :error="v$.fileSelection.$invalid"
                  />
                </v-col>

                <v-col :sm="2" :md="2">
                  <v-select v-model="selectedFileType"
                            :items="supportedFileTypes"
                            :error="v$.selectedFileType.$invalid"/>
                </v-col>

                <v-col :sm="2" :md="2">
                  <v-select v-model="selectedFileEncoding"
                            :items="supportedFileEncodings"
                            :error="v$.selectedFileEncoding.$invalid"/>
                </v-col>

                <v-col :sm="2" :md="1">
                  <v-btn @click="onStartPreview"
                         color="primary"
                         :loading="isUploading"
                         :disabled="v$.$invalid"
                         prepend-icon="mdi-file-eye"
                         text="Vorschau"
                  />
                </v-col>
              </v-row>

              <v-row>
                <raw-csv-file-table :file="fileSelection ?? undefined"
                                    :encoding="selectedFileEncoding"
                                    :is-loading="isUploading"
                                    @isLoading="onIsLoading"/>
              </v-row>
            </v-container>
          </div>

          <div v-else>
            <v-card v-if="isReadilyLoaded" id="preview-card"
                    body-class="p-2">
              <v-card-title class="d-flex justify-space-between py-2" id="preview-card-header">
                <h6>{{ parsedPreview.filename }}</h6>
                <h6>{{ parsedPreview.format }}</h6>
              </v-card-title>

              <v-card-text id="preview-card-body" class="p-2">
                <turnover-preview-table v-if="previewedData && categories"
                                        v-model:value="previewedData"
                                        :categories="categories"
                                        @onCreateCategory="onCreateCategory"/>
              </v-card-text>
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions>
          <div v-if="parsedPreview" class="w-100" style="display: flex;">
            <v-container class="p-0">
              <v-row>
                <v-col class="p-0 px-2">
                  <h6>Importierbar</h6>
                  <v-progress-linear :max="rawRows.length"
                                     :show-value="true"
                                     :buffer-value="rawRows.length - importableRows.length"
                                     :model-value="importableRows.length"
                  >
                    <!--                    <b-progress-bar :value="importableRows.length" variant="success"/>-->
                    <!--                    <b-progress-bar :value="rawRows.length - importableRows.length" variant="secondary"/>-->
                  </v-progress-linear>
                </v-col>

                <v-col class="p-0 px-2">
                  <h6>Kategorisiert</h6>
                  <v-progress-linear :max="importableRows.length"
                                     :show-value="true"
                                     :buffer-value="rowsTodo.length"
                                     :model-value="importableRows.length - rowsTodo.length">
                    <!--                    <b-progress-bar :value="importableRows.length - rowsTodo.length" variant="success"/>-->
                    <!--                    <b-progress-bar :value="rowsTodo.length" variant="danger"/>-->
                  </v-progress-linear>
                </v-col>
              </v-row>
            </v-container>

            <div class="m-auto">
              <v-btn class="mr-1"
                     color="secondary"
                     @click="reset">
                Abbrechen
              </v-btn>
              <v-btn color="primary"
                     @click="doImportRequest"
                     :disabled="isImportImpossible">
                Importieren
              </v-btn>
            </div>
          </div>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>

<style scoped>

</style>
