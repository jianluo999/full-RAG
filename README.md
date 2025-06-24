# 🧠 智能学习助手 Ultra - 你的本地AI学习伙伴

[![Stargazers](https://img.shields.io/github/stars/jianluo999/full-RAG?style=social)](https://github.com/jianluo999/full-RAG/stargazers)
[![Forks](https://img.shields.io/github/forks/jianluo999/full-RAG?style=social)](https://github.com/jianluo999/full-RAG/network/members)

你好，我是作者 **jianluo**，很高兴在这里与你分享这个我独立开发的项目！

你是否也曾为了复习堆积如山的PDF资料而烦恼？是否也曾希望有一个能24小时陪你学习、为你划重点、甚至考考你的"高配版学伴"？

这个想法驱动我创造了 **智能学习助手 Ultra**。它是一个完全在**你的电脑本地运行**的AI应用，这意味着：
*   **绝对隐私**：你所有的学习资料和对话记录都只存在于你的电脑，不会上传到任何服务器。
*   **完全免费**：利用本地大语言模型驱动，无需支付任何API费用。
*   **高度定制**：你可以喂给它任何你想要学习的PDF文档，让它成为你专属的、任何领域的知识专家。

---

## ✨ 核心功能

*   **智能PDF阅读**：轻松上传PDF文档，AI自动解析并存入你的私人知识库。
*   **精准知识问答**：基于你上传的文档内容，与AI进行流畅的多轮对话，深度挖掘知识点。
*   **长时对话记忆**：独有的"对话摘要"技术，让AI在长篇对话中也能保持记忆，理解上下文。
*   **AI随堂测验**：一键让AI根据文档内容为你出题，检验你的学习成果，支持选择题、简答题、名词解释等多种题型。
*   **本地模型驱动**：支持Ollama，可自由选择并切换适合你硬件的本地大语言模型。

---

## 🚀 快速开始

### 环境配置

在运行本项目前，请确保你的电脑上已经安装了以下环境：

1.  **Java Development Kit (JDK)**: `v17` 或更高版本。
2.  **Apache Maven**: `v3.8` 或更高版本，用于构建后端项目。
3.  **Node.js**: `v18` 或更高版本，用于构建前端项目。
4.  **Ollama**: 用于驱动AI功能。
    *   访问 [ollama.com](https://ollama.com) 下载并安装。
    *   安装后，请从 [Ollama Library](https://ollama.com/library) 拉取一个你需要的模型，例如 `llama3:8b`。
      ```bash
      ollama pull llama3:8b
      ```

### 项目部署

1.  **克隆仓库**
    ```bash
    git clone https://github.com/jianluo999/full-RAG.git
    cd full-RAG
    ```

2.  **启动后端 (Spring Boot)**
    打开一个新的终端窗口：
    ```bash
    cd backend
    mvn spring-boot:run
    ```
    后端服务默认运行在 `http://localhost:8080`。

3.  **启动前端 (Vue)**
    打开另一个新的终端窗口：
    ```bash
    cd frontend/study-assistant-ui
    npm install
    npm run dev
    ```
    前端开发服务器将启动，你可以在浏览器中访问 `http://localhost:5173` (或终端提示的地址) 来使用本应用。

---

## 📸 功能截图

**1. 上传文档 & AI出题**
**2. 智能问答**
![image](https://github.com/user-attachments/assets/7abf29b8-f881-4b50-8769-7f408a89f47d)

---

## 🛠️ 技术栈

*   **后端**: Java, Spring Boot
*   **前端**: Vue.js, Vite
*   **AI核心**: Spring AI
*   **本地模型支持**: Ollama
*   **数据库**: H2 (内存数据库)

---

## 💡 设计思路与未来

本项目采用了 **RAG (Retrieval-Augmented Generation)** 架构，当用户提问时，系统会首先从用户上传的私有知识库中检索最相关的内容，然后将这些内容和问题一起交给本地大模型去生成最终答案，确保了回答的准确性和时效性。

未来，我们还计划探索更多有趣的功能，例如：
*   **OCR支持**：让应用也能阅读扫描版的PDF。
*   **知识图谱**：用更酷的方式组织知识，实现更复杂的推理。
*   **更多题型**：引入判断题、填空题等。

---

## 💖 致谢与分享

感谢你的使用！如果你觉得这个小工具有趣或对你有帮助，欢迎：
*   在GitHub上给我一个 **Star** ⭐！
*   分享它，让更多人看到。
*   提出你的建议和想法，或者参与到开发中来！

Enjoy learning! 
