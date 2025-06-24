@echo off
CHCP 65001
ECHO "======================================================="
ECHO.
ECHO "            🧠 智能学习助手 Ultra 启动中..."
ECHO.
ECHO "   这个黑色的窗口是应用的后台服务，请不要关闭它。"
ECHO "   如果想关闭应用，直接关闭这个窗口即可。"
ECHO.
ECHO "======================================================="

:: 设置Java和Node.js的路径 (如果它们没有在系统环境变量中)
:: set JAVA_HOME="C:\path\to\your\jdk"
:: set NODE_HOME="C:\path\to\your\nodejs"
:: set PATH=%JAVA_HOME%\bin;%NODE_HOME%;%PATH%

:: 1. 启动后端 (在后台运行)
ECHO "正在启动后端Java服务..."
start "Backend" /B java -jar backend.jar

:: 等待几秒钟，确保后端服务有足够的时间启动
ECHO "等待后端服务预热 (约15秒)..."
timeout /t 15 /nobreak > nul

:: 2. 启动前端
ECHO "正在启动前端Web服务..."
ECHO "如果这是你第一次运行，可能会有一个安装提示，请耐心等待。"
start "Frontend" /B npx serve dist -l 5173

:: 3. 打开浏览器
ECHO "正在打开浏览器..."
timeout /t 3 /nobreak > nul
start http://localhost:5173

ECHO "🎉 启动完成！请在打开的浏览器窗口中使用应用。"
ECHO "   如果浏览器没有自动打开，请手动访问 http://localhost:5173"

ECHO.
ECHO "按任意键退出此启动向导... (后台服务将继续运行)"
pause > nul
exit 