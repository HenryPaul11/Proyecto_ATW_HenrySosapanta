<template>
  <form @submit.prevent="handleLogin">
    <div class="form-group">
      <label for="usuario">Nombre de Usuario</label>
      <input
        id="usuario"
        v-model="form.usuario"
        type="text"
        placeholder="Ingresa tu usuario"
        required
        autocomplete="username"
      />
    </div>

    <div class="form-group">
      <label for="contrasena">Contraseña</label>
      <input
        id="contrasena"
        v-model="form.contrasena"
        type="password"
        placeholder="Ingresa tu contraseña"
        required
        autocomplete="current-password"
      />
    </div>

    <button type="submit" :disabled="loading">
      {{ loading ? 'Ingresando...' : 'Iniciar Sesión' }}
    </button>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const auth = useAuthStore()

const form = ref({ usuario: '', contrasena: '' })
const errorMessage = ref('')
const loading = ref(false)

const MOCK_USERS = [
  { usuario: 'admin',     contrasena: '1234', rol: 'admin' },
  { usuario: 'juanperez', contrasena: '1234', rol: 'cliente' },
]

function handleLogin() {
  errorMessage.value = ''
  loading.value = true

  setTimeout(() => {
    const match = MOCK_USERS.find(
      u => u.usuario === form.value.usuario && u.contrasena === form.value.contrasena
    )

    if (match) {
      auth.login(match)
      router.push(match.rol === 'admin' ? '/dashboard' : '/dashboard')
    } else {
      errorMessage.value = 'Usuario o contraseña incorrectos.'
    }

    loading.value = false
  }, 600)
}
</script>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  color: #475569;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 6px;
  letter-spacing: 0.3px;
}

input[type='text'],
input[type='password'] {
  width: 100%;
  box-sizing: border-box;
  padding: 12px 14px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  outline: none;
  background: #f8fafc;
  color: #1e293b;
  font-weight: 500;
}

input[type='text']:focus,
input[type='password']:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background: white;
}

button[type='submit'] {
  width: 100%;
  box-sizing: border-box;
  padding: 14px;
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  margin-top: 4px;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.4);
}

button[type='submit']:hover:not(:disabled) {
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.5);
}

button[type='submit']:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  box-sizing: border-box;
  width: 100%;
  color: #ef4444;
  padding: 12px 16px;
  border-radius: 12px;
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  border-left: 4px solid #ef4444;
  background: #fef2f2;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25%       { transform: translateX(-8px); }
  75%       { transform: translateX(8px); }
}
</style>
