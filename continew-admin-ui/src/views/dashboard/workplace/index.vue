<template>
  <div class="gi_page workplace-container">
    <div class="welcome-section">
      <Welcome />
    </div>
    <div class="content-section">
      <a-row :gutter="16">
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <MessageList />
        </a-col>
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <NoticeList />
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import Welcome from './components/Welcome.vue'
import MessageList from './components/MessageList.vue'
import NoticeList from './components/NoticeList.vue'

defineOptions({ name: 'Workplace' })
</script>

<style scoped lang="scss">
.workplace-container {
  width: 100%;
  max-width: 100%;
  padding: 0;
}

.welcome-section {
  margin-bottom: 16px;
  background-color: var(--color-bg-2);
  border-radius: 4px;
  overflow: auto;
}

.content-section {
  :deep(.arco-col) {
    margin-bottom: 16px;
  }
}
</style>
