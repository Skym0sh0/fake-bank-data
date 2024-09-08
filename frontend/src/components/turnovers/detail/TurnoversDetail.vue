<template>
  <div>
    <waiting-indicator :is-loading="isLoading"/>

    <v-card v-if="!!turnoverImport">
      <v-card-title>
        Importiere Umsatz Datei
      </v-card-title>

      <v-card-subtitle>
        <div class="d-flex justify-content-between">
          <span>
            {{ turnoverImport.format }}: "{{ turnoverImport.filename }}" ({{ turnoverImport.encoding }})
          </span>

          <span>
            {{ importTimestamp }}
          </span>
        </div>
      </v-card-subtitle>

      <v-card-text>
        <turnover-rows-table v-if="categories"
                             :rows="turnoverImport.turnovers"
                             :touchedRowsIdsById="currentRowCategoryChangesById"
                             :deletedRowsIdsById="currentDeletedRowsById"
                             :categories="categories"
                             @onCreateCategory="onCreateCategory"
                             @deleteTurnover="onDeleteTurnover"
                             @undoDeleteTurnover="onUndoDeleteTurnover"/>
      </v-card-text>

      <v-card-actions class="d-flex justify-content-between">
        <confirmationed-button @click="onReset"
                               default-caption="Verwerfen"
                               request-caption="Verwerfen??"
                               confirmed-caption="Jetzt Verwerfen!!!"
                               :wait-time-ms="250"
                               default-color="error"
                               :small="true"
                               :disabled="!isValidToSave"/>

        <div class="d-flex" style="gap: 0.5em">
          <v-btn @click="onBack">
            Zurück
          </v-btn>
          <confirmationed-button @click="onSave"
                                 :wait-time-ms="500"
                                 default-caption="Speichern"
                                 request-caption="Umsätze überschreiben??"
                                 confirmed-caption="Jetzt überschreiben"
                 :disabled="!isValidToSave"
                 color="info">
            Speichern
          </confirmationed-button>
        </div>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import TurnoverRowsTable from "@/components/turnovers/detail/TurnoverRowsTable.vue";
import {normalizeCategory} from "@/util/Normalizer";
import moment from "moment";
import ConfirmationedButton from "@/components/misc/ConfirmationedButton.vue";

export default {
  name: "TurnoversDetail",
  components: {
    ConfirmationedButton,
    TurnoverRowsTable,
    WaitingIndicator,
  },
  props: {
    id: {
      type: String,
      required: true,
    }
  },
  data() {
    return {
      isLoading: false,
      turnoverImport: null,
      categories: null,

      initialTurnoverRowsCategories: [],
      deleteRowsWithIds: [],
    }
  },
  methods: {
    reload() {
      this.isLoading = true

      Promise.all([this.loadImport(), this.loadCategories()])
        .finally(() => this.isLoading = false)
    },
    loadImport() {
      return api.getTurnovers()
        .fetchTurnoverImport(this.id)
        .then(res => {
          this.turnoverImport = res
          this.deleteRowsWithIds = []
          this.initialTurnoverRowsCategories = this.extractTurnoverRowsWithCategories(res)
        })
    },
    loadCategories() {
      return api.getCategories()
        .fetchCategoryTree()
        .then(res => {
          this.categories = res
        })
    },
    onCreateCategory(categoryName) {
      const normalized = normalizeCategory({
        name: categoryName,
      });

      this.isLoading = true
      api.getCategories()
        .postCategory(normalized)
        .then(() => this.loadCategories())
        .finally(() => this.isLoading = false)
    },
    onDeleteTurnover(row) {
      this.deleteRowsWithIds.push(row.id)
    },
    onUndoDeleteTurnover(row) {
      let idx;
      while ((idx = this.deleteRowsWithIds.indexOf(row.id)) >= 0) {
        if (idx < 0)
          return;
        this.deleteRowsWithIds.splice(idx, 1)
      }
    },
    onReset() {
      this.reload()
    },
    onBack() {
      this.$router.back()
    },
    onSave() {
      if (!this.isValidToSave)
        return;

      const changes = {rows: this.currentRowCategoryChanges, deleteRowIds: this.deleteRowsWithIds};

      this.isLoading = true
      api.getTurnovers()
        .patchTurnovers(this.id, changes)
        .then(() => this.loadImport())
        .finally(() => this.isLoading = false)
    },
    extractTurnoverRowsWithCategories(turnoverImport) {
      if (!turnoverImport || !turnoverImport.turnovers)
        return [];

      return turnoverImport.turnovers.map(row => ({
        id: row.id,
        categoryId: row.categoryId,
      }))
    },
  }
  ,
  computed: {
    importTimestamp() {
      return moment(this.turnoverImport.importedAt).format("YYYY-MM-DD HH:mm:ss.SSS");
    }
    ,
    currentRowCategoryChanges() {
      const rowsById = this.initialTurnoverRowsCategories.reduce((prev, cur) => ({...prev, [cur.id]: cur}), {})

      return this.extractTurnoverRowsWithCategories(this.turnoverImport)
        .filter(row => rowsById[row.id].categoryId !== row.categoryId);
    }
    ,
    currentRowCategoryChangesById() {
      return this.currentRowCategoryChanges.reduce((prev, cur) => ({...prev, [cur.id]: cur}), {})
    }
    ,
    currentDeletedRowsById() {
      return this.deleteRowsWithIds.reduce((prev, cur) => ({...prev, [cur]: cur}), {})
    }
    ,
    isValidToSave() {
      return this.deleteRowsWithIds.length > 0
        || (this.currentRowCategoryChanges.length > 0
          && this.currentRowCategoryChanges.every(row => !!row.categoryId))
    }
    ,
  }
  ,
  mounted() {
    this.reload()
  }
}
</script>

<style scoped>

</style>
