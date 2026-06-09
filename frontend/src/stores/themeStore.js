import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const dark = ref(localStorage.getItem('theme') === 'dark')

  // Aplica/quita la clase en <html> al cambiar
  watch(dark, (val) => {
    document.documentElement.classList.toggle('dark', val)
    localStorage.setItem('theme', val ? 'dark' : 'light')
  }, { immediate: true })

  function toggle() {
    dark.value = !dark.value
  }

  return { dark, toggle }
})
