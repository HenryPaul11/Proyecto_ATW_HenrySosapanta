<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-blue-900 px-5 py-10 relative overflow-hidden">

    <!-- Círculos decorativos -->
    <div class="absolute top-0 right-0 w-[40vw] max-w-[400px] aspect-square rounded-full bg-white/10 -translate-y-1/2 translate-x-1/2 pointer-events-none animate-float" />
    <div class="absolute bottom-0 left-0 w-[35vw] max-w-[350px] aspect-square rounded-full bg-white/5 translate-y-1/2 -translate-x-1/2 pointer-events-none animate-float-reverse" />

    <!-- Card -->
    <div class="relative z-10 w-full max-w-sm bg-white rounded-3xl shadow-2xl px-6 py-8 sm:px-10 sm:py-12 text-center animate-slide-in">

      <!-- Ícono -->
      <div class="text-5xl sm:text-6xl mb-3 inline-block animate-pulse-slow">🏋️</div>

      <h2 class="text-xl sm:text-2xl font-extrabold text-blue-500 tracking-tight mb-6">
        Iniciar Sesión
      </h2>

      <LoginForm />
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../components/login/LoginForm.vue'
</script>

<style scoped>
@keyframes float {
  0%, 100% { transform: translateY(0)   translateX(50%)  ; }
  50%       { transform: translateY(30px) translateX(50%) ; }
}
@keyframes float-reverse {
  0%, 100% { transform: translateY(0)    translateX(-50%); }
  50%       { transform: translateY(-30px) translateX(-50%); }
}
@keyframes slide-in {
  from { opacity: 0; transform: translateY(40px); }
  to   { opacity: 1; transform: translateY(0);    }
}
@keyframes pulse-slow {
  0%, 100% { transform: scale(1);    }
  50%       { transform: scale(1.06); }
}

.animate-float         { animation: float         6s ease-in-out infinite; }
.animate-float-reverse { animation: float-reverse 8s ease-in-out infinite; }
.animate-slide-in      { animation: slide-in      0.6s ease-out both;      }
.animate-pulse-slow    { animation: pulse-slow    2s  ease-in-out infinite; }
</style>
