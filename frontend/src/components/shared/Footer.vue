<template>
  <footer class="footer">
    <p>Gimnasio PowerFit &copy; {{ year }} - Todos los derechos reservados</p>
    <p class="footer-sub">Horario: Lunes a Viernes 5:00 AM - 10:00 PM | Sábados y Domingos 7:00 AM - 8:00 PM</p>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  background: var(--navbar-bg);
  color: var(--text-muted);
  text-align: center;
  padding: 25px;
  margin-top: 60px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  font-weight: 500;
  border-top: 3px solid #3b82f6;
  transition: background 0.3s ease;
}

.footer p { margin: 8px 0; }
.footer-sub { color: #94a3b8; font-size: 13px; }
</style>
