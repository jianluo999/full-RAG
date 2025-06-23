<script setup>
import { ref } from 'vue';
import axios from 'axios';

const selectedFile = ref(null);
const extractedText = ref('');
const errorMessage = ref('');

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
</script>

<template>
  <div class="container">
    <h1>PDF Content Extractor</h1>
    <div class="upload-section">
      <input type="file" @change="onFileChange" accept=".pdf" />
      <button @click="uploadFile">Upload and Extract</button>
    </div>
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    <div v-if="extractedText" class="text-display">
      <h2>Extracted Text:</h2>
      <pre>{{ extractedText }}</pre>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  background-color: #2f2f2f;
  border-radius: 8px;
}

h1 {
  color: #646cff;
}

.upload-section {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  align-items: center;
}

button {
  padding: 0.6em 1.2em;
  font-size: 1em;
  font-weight: 500;
  font-family: inherit;
  background-color: #1a1a1a;
  cursor: pointer;
  border: 1px solid transparent;
  border-radius: 8px;
  transition: border-color 0.25s;
}
button:hover {
  border-color: #646cff;
}

.error-message {
  margin-top: 1rem;
  color: #ff6b6b;
}

.text-display {
  margin-top: 2rem;
  text-align: left;
  background-color: #242424;
  padding: 1rem;
  border-radius: 4px;
  white-space: pre-wrap; /* Wrap long lines */
  word-wrap: break-word;
}

pre {
  color: #e0e0e0;
}
</style> 