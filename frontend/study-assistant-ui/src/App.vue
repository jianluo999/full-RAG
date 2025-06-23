<script setup>
import { ref } from 'vue';
import axios from 'axios';

const selectedFile = ref(null);
const extractedText = ref('');
const errorMessage = ref('');

// 新增：搜索相关的响应式变量
const searchQuery = ref('');
const searchResults = ref([]);
const searchError = ref('');
const isSearching = ref(false);

function onFileChange(event) {
  selectedFile.value = event.target.files[0];
  errorMessage.value = '';
  extractedText.value = '';
}

async function uploadFile() {
  if (!selectedFile.value) {
    errorMessage.value = 'Please select a file first.';
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    // Note: We'll need to configure a proxy in vite.config.js to avoid CORS issues.
    const response = await axios.post('/api/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    extractedText.value = response.data;
  } catch (error) {
    console.error('Error uploading file:', error);
    if (error.response) {
      errorMessage.value = `Error: ${error.response.data}`;
    } else {
      errorMessage.value = 'Could not connect to the server. Is it running?';
    }
  }
}

// 新增：搜索功能
async function searchDocuments() {
  if (!searchQuery.value.trim()) {
    searchError.value = 'Please enter a search term.';
    return;
  }

  isSearching.value = true;
  searchError.value = '';

  try {
    const response = await axios.get('/api/query', {
      params: {
        q: searchQuery.value.trim()
      }
    });
    searchResults.value = response.data;
    if (searchResults.value.length === 0) {
      searchError.value = 'No documents found for your query.';
    }
  } catch (error) {
    console.error('Error searching documents:', error);
    searchError.value = 'Error searching documents. Please try again.';
  } finally {
    isSearching.value = false;
  }
}

// 新增：清空搜索结果
function clearSearch() {
  searchQuery.value = '';
  searchResults.value = [];
  searchError.value = '';
}
</script>

<template>
  <div class="container">
    <h1>Study Assistant - 智能学习助手</h1>
    
    <!-- 文件上传区域 -->
    <div class="section">
      <h2>📄 上传PDF文档</h2>
      <div class="upload-section">
        <input type="file" @change="onFileChange" accept=".pdf" />
        <button @click="uploadFile">Upload and Extract</button>
      </div>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      <div v-if="extractedText" class="success-message">
        {{ extractedText }}
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="section">
      <h2>🔍 搜索知识库</h2>
      <div class="search-section">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="输入关键词搜索已上传的文档..." 
          @keyup.enter="searchDocuments"
          class="search-input"
        />
        <button @click="searchDocuments" :disabled="isSearching" class="search-btn">
          {{ isSearching ? '搜索中...' : '搜索' }}
        </button>
        <button @click="clearSearch" class="clear-btn">清空</button>
      </div>
      
      <div v-if="searchError" class="error-message">
        {{ searchError }}
      </div>

      <!-- 搜索结果显示 -->
      <div v-if="searchResults.length > 0" class="results-section">
        <h3>搜索结果 ({{ searchResults.length }} 个相关文档):</h3>
        <div v-for="doc in searchResults" :key="doc.id" class="result-item">
          <div class="result-header">
            <h4>📋 {{ doc.fileName }}</h4>
            <span class="doc-id">ID: {{ doc.id }}</span>
          </div>
          <div class="result-content">
            {{ doc.content.substring(0, 300) }}{{ doc.content.length > 300 ? '...' : '' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1000px;
  margin: 2rem auto;
  padding: 2rem;
  background-color: #2f2f2f;
  border-radius: 8px;
}

h1 {
  color: #646cff;
  text-align: center;
  margin-bottom: 3rem;
}

.section {
  margin-bottom: 3rem;
  padding: 2rem;
  background-color: #3a3a3a;
  border-radius: 8px;
}

.section h2 {
  color: #e0e0e0;
  margin-bottom: 1.5rem;
}

.upload-section, .search-section {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 0.8rem;
  font-size: 1rem;
  border: 1px solid #555;
  border-radius: 4px;
  background-color: #1a1a1a;
  color: #e0e0e0;
}

button {
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  font-family: inherit;
  background-color: #1a1a1a;
  color: #e0e0e0;
  cursor: pointer;
  border: 1px solid transparent;
  border-radius: 4px;
  transition: all 0.25s;
}

button:hover:not(:disabled) {
  border-color: #646cff;
  background-color: #646cff;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.search-btn {
  background-color: #4CAF50;
}

.clear-btn {
  background-color: #f44336;
}

.error-message {
  margin-top: 1rem;
  color: #ff6b6b;
  padding: 0.8rem;
  background-color: rgba(255, 107, 107, 0.1);
  border-radius: 4px;
}

.success-message {
  margin-top: 1rem;
  color: #4CAF50;
  padding: 0.8rem;
  background-color: rgba(76, 175, 80, 0.1);
  border-radius: 4px;
}

.results-section {
  margin-top: 2rem;
}

.results-section h3 {
  color: #4CAF50;
  margin-bottom: 1rem;
}

.result-item {
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background-color: #242424;
  border-radius: 6px;
  border-left: 4px solid #646cff;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.result-header h4 {
  color: #646cff;
  margin: 0;
}

.doc-id {
  color: #888;
  font-size: 0.9rem;
}

.result-content {
  color: #e0e0e0;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style> 