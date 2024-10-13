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
import {apiRefKey, notifierRefKey} from "../../../keys.ts";
import WaitingIndicator from "../../misc/WaitingIndicator.vue";
import useVuelidate from "@vuelidate/core";
import {required} from '@vuelidate/validators'
import RawCsvFileTable from "./RawCsvFileTable.vue";
import TurnoverPreviewTable from "./TurnoverPreviewTable.vue";
import Histogram, {HistogramValueType} from "./Histogram.vue";

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

export type PreviewRowWithOriginalState = TurnoverRowPreview & { originalImportable?: boolean; index: number; };

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;
const categoriesApi: CategoryApi | undefined = inject(apiRefKey)?.categoriesApi;
const notifierRef = inject(notifierRefKey);

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
const filterShowImportedRows = ref<boolean>(false);

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

const importablesHistogram = computed<HistogramValueType[]>(() => {
  return [
    {
      label: 'Importierbare Zeilen',
      value: importableRows.value.length,
      color: 'success'
    },
    {
      label: 'Nicht importierbare Zeilen',
      value: rawRows.value.length - importableRows.value.length,
      color: 'info'
    }
  ]
})

const progressHistogram = computed<HistogramValueType[]>(() => {
  return [
    {
      label: 'Schon fertig bearbeitete Zeilen',
      value: importableRows.value.length - rowsTodo.value.length,
      color: 'success'
    },
    {
      label: 'Noch zu bearbeitende Zeilen',
      value: rowsTodo.value.length,
      color: 'error'
    }
  ]
})

function loadCategories() {
  return categoriesApi?.getCategoriesAsTree()
    .then(res => {
      categories.value = res.data;
    })
}

function onIsLoading(isLoading: boolean) {
  isUploading.value = isLoading
}

function onStartPreview() {
  isUploading.value = true;

  Promise.all([loadCategories(), doPreviewRequest()])
    .catch(e => notifierRef?.notifyError("Vorschau konnte nicht geladen werden", e))
    .finally(() => isUploading.value = false)
}

function doPreviewRequest() {
  return api?.processPreview(selectedFileType.value!, fileSelection.value!, selectedFileEncoding.value)
    .then(preview => {
      parsedPreview.value = preview.data;

      previewedData.value = (parsedPreview.value.rows || [])
        .map((row: TurnoverRowPreview, index: number) => ({
          ...row,
          index: index,
          originalImportable: row.importable
        }))
    })
    .catch(e => notifierRef?.notifyError("Vorschau konnte nicht verarbeitet werden", e))
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
    .catch(e => notifierRef?.notifyError("Umsätze konnten nicht importiert werden", e))
    .finally(() => isUploading.value = false)
}

function onCreateCategory({categoryName, callback}: { categoryName: string; callback?: () => void; }) {
  const normalized: CategoryPatch = {
    name: categoryName,
  };

  isUploading.value = true;

  return categoriesApi?.createCategory(normalized)
    .catch(e => notifierRef?.notifyError(`Neue Kategorie ${categoryName} konnte nicht erstellt werden`, e))
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
  isUploading.value = false;
  // this.$refs["file-upload-modal"].hide();
}

function openDialog() {
  reset();
  isDialogOpen.value = true
}

onMounted(() => {
  isUploading.value = true;

  api?.getSupportedFormats()
    .then(res => {
      supportedFileTypes.value = [
        {
          value: null,
          title: "Dateiformat wählen",
        },
        ...res.data.map(f => ({
          value: f,
          title: getBankFormatName(f)
        }))
      ];
    })
    .catch(e => notifierRef?.notifyError("Unterstützte Formate konnten nicht geladen werden", e))
    .finally(() => isUploading.value = false)
})
</script>

<template>
  <v-dialog v-model="isDialogOpen"
            :persistent="true"
            @update:model-value="reset">
    <template v-slot:activator>
      <v-btn size="small"
             color="primary"
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

        <v-card-text class="py-0">
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
            <v-card v-if="isReadilyLoaded"
                    id="preview-card"
                    :elevation="2">
              <v-card-title class="d-flex justify-space-between align-center py-2" id="preview-card-header">
                <h6>{{ parsedPreview.filename }}</h6>
                <h6>{{ parsedPreview.format }}</h6>

                <v-checkbox v-model="filterShowImportedRows"
                            color="primary"
                            label="Zeige schon importierte Zeilen"
                            density="compact"
                            :hide-details="true"/>
              </v-card-title>

              <v-card-text id="preview-card-body">
                <turnover-preview-table v-if="previewedData && categories"
                                        v-model:value="previewedData"
                                        :show-already-imported-row="filterShowImportedRows"
                                        :categories="categories"
                                        @onCreateCategory="onCreateCategory"/>
              </v-card-text>
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions v-if="parsedPreview" class="w-100 d-flex justify-space-between">
          <v-container :fluid="true">
            <v-row>
              <v-col>
                <Histogram text="Importierbar" :values="importablesHistogram"/>
              </v-col>

              <v-col class="p-0 px-2">
                <Histogram text="Kategorisiert" :values="progressHistogram"/>
              </v-col>

              <v-col cols="6"/>
            </v-row>
          </v-container>

          <div class="d-flex justify-space-between align-center ga-1">
            <v-btn color="secondary"
                   variant="outlined"
                   @click="reset">
              Abbrechen
            </v-btn>
            <v-btn color="primary"
                   variant="outlined"
                   @click="doImportRequest"
                   :disabled="isImportImpossible">
              Importieren
            </v-btn>
          </div>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>

<style scoped>

</style>
