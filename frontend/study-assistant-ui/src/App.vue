<script setup>
import { ref, nextTick } from 'vue';
import axios from 'axios';
import { marked } from 'marked';

// File Upload State
const selectedFile = ref(null);
const isUploading = ref(false);
const uploadStatus = ref('');

// Search State
const searchQuery = ref('');
const searchResults = ref([]);
const isSearching = ref(false);
const searchError = ref('');

// Chat State
const currentQuery = ref('');
const chatHistory = ref([]);
const isThinking = ref(false);
const aiError = ref('');
const chatHistoryContainer = ref(null);


const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
  uploadStatus.value = '';
};

const uploadAndExtract = async () => {
  if (!selectedFile.value) return;
  isUploading.value = true;
  uploadStatus.value = '';
  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    const response = await axios.post('/api/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    uploadStatus.value = response.data;
  } catch (error) {
    uploadStatus.value = `上传文件出错: ${error.response?.data || error.message}`;
  } finally {
    isUploading.value = false;
  }
};

const searchDocuments = async () => {
  if (!searchQuery.value.trim()) return;
  isSearching.value = true;
  searchError.value = '';
  searchResults.value = [];
  try {
    const response = await axios.post('/api/query', { query: searchQuery.value });
    searchResults.value = response.data;
    if (response.data.length === 0) {
      searchError.value = "没有找到相关的文档。"
    }
  } catch (error) {
    searchError.value = `搜索出错: ${error.response?.data || error.message}`;
  } finally {
    isSearching.value = false;
  }
};

const clearSearch = () => {
  searchQuery.value = '';
  searchResults.value = [];
  searchError.value = '';
};

const scrollToBottom = () => {
  nextTick(() => {
    const container = chatHistoryContainer.value;
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  });
};

const askAI = async () => {
  const userQuery = currentQuery.value.trim();
  if (!userQuery) return;

  // Check if there are search results to provide context
  if (searchResults.value.length === 0) {
    aiError.value = "无法在没有搜索结果的情况下提问。请先搜索一个主题。";
    return;
  }

  isThinking.value = true;
  aiError.value = '';
  
  // Add user message to history
  chatHistory.value.push({ role: 'user', content: userQuery });
  currentQuery.value = '';
  scrollToBottom();

  try {
    const payload = {
      query: userQuery,
      history: chatHistory.value.slice(0, -1), // Send history BEFORE the current question
      contexts: searchResults.value.map(doc => doc.content)
    };
    const response = await axios.post('/api/rag/query', payload);
    
    // Add assistant response to history
    chatHistory.value.push({ role: 'assistant', content: response.data });
  } catch (error) {
    const errorMessage = `AI 出错: ${error.response?.data || error.message}`;
    aiError.value = errorMessage;
    chatHistory.value.push({ role: 'assistant', content: `抱歉，出错了。${errorMessage}` });
  } finally {
    isThinking.value = false;
    scrollToBottom();
  }
};

const renderMarkdown = (content) => {
    return marked(content);
}

</script>

<template>
  <div id="app">
    <header>
      <h1>📚 智能学习助手 Ultra</h1>
      <p>你的本地 RAG 学习伙伴</p>
    </header>

    <main>
      <!-- File Upload Section -->
      <section class="card">
        <h2>1. 上传文档</h2>
        <div class="upload-container">
          <input type="file" @change="handleFileChange" accept=".pdf" ref="fileInput" style="display: none" />
          <button @click="$refs.fileInput.click()">选择 PDF</button>
          <span v-if="selectedFile" class="file-name">{{ selectedFile.name }}</span>
          <button @click="uploadAndExtract" :disabled="!selectedFile || isUploading">
            <span v-if="isUploading">上传中...</span>
            <span v-else>上传并保存</span>
          </button>
        </div>
        <p v-if="uploadStatus" :class="['status-message', uploadStatus.includes('success') || uploadStatus.includes('成功') ? 'success' : 'error']">{{ uploadStatus }}</p>
      </section>

      <!-- Search and RAG Section -->
      <section class="card">
        <h2>2. 搜索与问答</h2>
        
        <!-- Search Results -->
        <div class="search-container">
          <input type="text" v-model="searchQuery" placeholder="在文档中搜索关键词..." @keyup.enter="searchDocuments" />
          <button @click="searchDocuments" :disabled="!searchQuery.trim()">搜索</button>
          <button @click="clearSearch" class="secondary">清空</button>
        </div>
        
        <div v-if="isSearching" class="status-message">搜索中...</div>
        <div v-if="searchError" class="status-message error">{{ searchError }}</div>

        <div v-if="searchResults.length > 0" class="search-results">
          <h3>搜索结果 ({{ searchResults.length }} 个相关文档):</h3>
          <ul>
            <li v-for="doc in searchResults" :key="doc.id">
              <details>
                <summary>📄 {{ doc.fileName }} (ID: {{ doc.id }})</summary>
                <p class="document-content">{{ doc.content }}</p>
              </details>
            </li>
          </ul>
        </div>

        <!-- Chat Interface -->
        <div class="chat-container">
          <div class="chat-history" ref="chatHistoryContainer">
            <div v-for="(message, index) in chatHistory" :key="index" :class="['chat-message', message.role]">
              <div class="message-content" v-html="renderMarkdown(message.content)"></div>
            </div>
             <div v-if="isThinking" class="chat-message assistant">
              <div class="message-content thinking"><span></span><span></span><span></span></div>
            </div>
          </div>
          <div class="chat-input">
            <input type="text" v-model="currentQuery" placeholder="针对以上内容提问..." @keyup.enter="askAI" :disabled="isThinking" />
            <button @click="askAI" :disabled="!currentQuery.trim() || isThinking">发送</button>
          </div>
           <div v-if="aiError" class="status-message error">{{ aiError }}</div>
        </div>
      </section>
    </main>
  </div>
</template>

<style>
:root {
  --primary-color: #42b883;
  --secondary-color: #35495e;
  --background-color: #f0f2f5;
  --card-background: #ffffff;
  --text-color: #2c3e50;
  --border-radius: 8px;
  --box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  --font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
}

body {
  font-family: var(--font-family);
  background-color: var(--background-color);
  color: var(--text-color);
  margin: 0;
  padding: 2rem;
}

#app {
  max-width: 800px;
  margin: 0 auto;
  display: grid;
  gap: 2rem;
}

header {
  text-align: center;
}

header h1 {
  color: var(--secondary-color);
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.card {
  background: var(--card-background);
  border-radius: var(--border-radius);
  padding: 1.5rem 2rem;
  box-shadow: var(--box-shadow);
}

h2 {
  color: var(--secondary-color);
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 0.5rem;
  margin-top: 0;
}

button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--border-radius);
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

button:hover:not(:disabled) {
  background-color: #36a476;
}

button:disabled {
  background-color: #a5d8c3;
  cursor: not-allowed;
}

button.secondary {
    background-color: #f3f4f6;
    color: var(--secondary-color);
    border: 1px solid #d1d5db;
}

button.secondary:hover:not(:disabled) {
    background-color: #e5e7eb;
}

input[type="text"] {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #d1d5db;
    border-radius: var(--border-radius);
    font-size: 1rem;
    box-sizing: border-box;
}

.upload-container, .search-container, .chat-input {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.file-name {
  font-style: italic;
  color: #555;
}

.status-message {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: var(--border-radius);
  text-align: center;
}

.status-message.success {
  background-color: #e6f7f0;
  color: #0d653f;
}

.status-message.error {
  background-color: #fdeaea;
  color: #c53030;
}

.search-results {
  margin-top: 1.5rem;
}

.search-results ul {
    list-style-type: none;
    padding: 0;
}

.search-results li summary {
    cursor: pointer;
    font-weight: 500;
}

.document-content {
    background: #fafafa;
    border-left: 3px solid var(--primary-color);
    padding: 1rem;
    margin-top: 0.5rem;
    white-space: pre-wrap;
    max-height: 150px;
    overflow-y: auto;
    font-size: 0.9rem;
}

.chat-container {
  margin-top: 2rem;
  border-top: 1px solid #e5e7eb;
  padding-top: 1.5rem;
}

.chat-history {
  height: 400px;
  overflow-y: auto;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: var(--border-radius);
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chat-message {
  display: flex;
  flex-direction: column;
}

.chat-message .message-content {
  max-width: 80%;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  line-height: 1.5;
}

.chat-message.user {
  align-items: flex-end;
}

.chat-message.user .message-content {
  background-color: var(--primary-color);
  color: white;
  border-bottom-right-radius: 2px;
}

.chat-message.assistant {
  align-items: flex-start;
}

.chat-message.assistant .message-content {
  background-color: #e5e7eb;
  color: var(--secondary-color);
  border-bottom-left-radius: 2px;
}

.message-content.thinking {
    display: flex;
    align-items: center;
    gap: 5px;
}

.message-content.thinking span {
    width: 8px;
    height: 8px;
    background-color: var(--secondary-color);
    border-radius: 50%;
    animation: bounce 1.4s infinite ease-in-out both;
}

.message-content.thinking span:nth-child(1) { animation-delay: -0.32s; }
.message-content.thinking span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}

.message-content p:first-child {
  margin-top: 0;
}
.message-content p:last-child {
  margin-bottom: 0;
}

</style> 