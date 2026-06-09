<template>
  <div class="login-bg">
    <div class="circle circle-top" />
    <div class="circle circle-bottom" />

    <div class="login-container">
      <div class="logo-wrapper">
        <span class="logo-icon"><img src="/icons/pesa.svg" alt="🏋️" class="w-16 h-16 mx-auto" style="filter:brightness(0) saturate(100%) invert(25%) sepia(80%) saturate(500%) hue-rotate(200deg) opacity(0.9)" /></span>
      </div>
      <h2>Iniciar Sesión</h2>
      <LoginForm />
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../components/login/LoginForm.vue'
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.circle-top {
  width: 500px;
  height: 500px;
  background: rgba(255, 255, 255, 0.1);
  top: -250px;
  right: -250px;
  animation: float 6s ease-in-out infinite;
}

.circle-bottom {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.05);
  bottom: -200px;
  left: -200px;
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50%       { transform: translateY(30px); }
}

.login-container {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  padding: 50px 45px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 440px;
  position: relative;
  z-index: 1;
  animation: slideIn 0.6s ease-out;
  text-align: center;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(50px); }
  to   { opacity: 1; transform: translateY(0); }
}

.logo-wrapper { margin-bottom: 16px; }

.logo-icon {
  font-size: 72px;
  display: inline-block;
  filter: drop-shadow(0 8px 24px rgba(59, 130, 246, 0.4));
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50%       { transform: scale(1.05); }
}

.login-container h2 {
  font-size: 32px;
  font-weight: 800;
  color: #3b82f6;
  letter-spacing: -0.5px;
  margin-bottom: 30px;
}

@media (max-width: 480px) {
  .login-container { padding: 40px 30px; }
  .login-container h2 { font-size: 26px; }
  .logo-icon { font-size: 56px; }
}
</style>
