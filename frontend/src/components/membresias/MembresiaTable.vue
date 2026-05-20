<template>
  <div class="w-full max-w-full mb-8">
    <p class="text-xs text-slate-400 text-right mb-1 sm:hidden">← desliza para ver más →</p>
    <div class="overflow-x-auto bg-white rounded-2xl shadow-sm">
      <table class="w-full border-collapse" style="min-width: 560px">
        <thead class="bg-gradient-to-r from-blue-500 to-blue-600">
          <tr>
            <th
              v-for="col in columns"
              :key="col"
              class="px-4 py-3 text-left text-white font-bold text-xs uppercase tracking-wider whitespace-nowrap"
            >
              {{ col }}
            </th>
          </tr>
        </thead>
        <tbody v-if="rows.length === 0">
          <tr>
            <td :colspan="columns.length" class="text-center py-10 text-slate-400 text-sm font-medium">
              {{ emptyText }}
            </td>
          </tr>
        </tbody>
        <slot v-else />
      </table>
    </div>
  </div>
</template>

<script setup>
defineProps({
  columns:   { type: Array,  required: true },
  rows:      { type: Array,  required: true },
  emptyText: { type: String, default: 'No hay datos.' },
})
</script>

<style>
.membresia-table-body tr {
  background: white;
  transition: background 0.15s ease;
}
.membresia-table-body tr:nth-child(even) { background: #f8fafc; }
.membresia-table-body tr:hover           { background: #eff6ff; }
.membresia-table-body tr:last-child td   { border-bottom: none; }
.membresia-table-body td {
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
  white-space: nowrap;
}
</style>
