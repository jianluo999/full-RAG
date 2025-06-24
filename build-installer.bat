@echo off
rem 设置代码页为UTF-8以正确显示中文
CHCP 65001 > nul
setlocal

rem --- 基本配置 ---
set "PROJECT_NAME=Study Assistant Ultra"
set "APP_VERSION=1.0.0"
set "VENDOR_NAME=StudyUltra"
set "JAR_NAME=study-assistant-0.0.1-SNAPSHOT.jar"
set "MAIN_CLASS=com.example.studyassistant.StudyAssistantApplication"
set "INSTALLER_DIR=installer"
set "TEMP_DIR=installer_temp"
set "ICON_PATH=frontend\study-assistant-ui\public\logo.ico"
set "JAR_PATH=backend\target\%JAR_NAME%"


echo =======================================================
echo       🚀 %PROJECT_NAME% 安装包生成脚本 🚀
echo   本脚本将自动编译打包项目，并创建EXE安装程序。
echo =======================================================

rem --- 确保我们在项目根目录 ---
cd /d "%~dp0"

echo.
echo [步骤 1/3] 正在清理和打包后端 (All-in-One JAR)...
cd backend
cmd /c "mvn clean install -DskipTests"
if errorlevel 1 (
    echo.
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    echo [错误] Maven打包失败，请检查上面的错误信息。
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    goto :end
)
echo [成功] 后端打包完成！
cd ..

echo.
echo [步骤 2/3] 正在准备安装程序所需文件...
rem 删除旧的临时目录和安装目录
if exist "%TEMP_DIR%" (
    rmdir /S /Q "%TEMP_DIR%"
)
if exist "%INSTALLER_DIR%" (
    rmdir /S /Q "%INSTALLER_DIR%"
)
mkdir "%TEMP_DIR%"
mkdir "%INSTALLER_DIR%"

rem 检查JAR文件是否存在
if not exist "%JAR_PATH%" (
    echo.
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    echo [错误] 找不到后端JAR文件: %JAR_PATH%
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    goto :end
)
echo   - 正在复制主程序JAR文件...
copy "%JAR_PATH%" "%TEMP_DIR%" > nul

echo   - 正在复制所有依赖JAR文件...
copy "backend\target\libs\*.jar" "%TEMP_DIR%" > nul

rem 检查并复制图标文件
if exist "%ICON_PATH%" (
    echo   - 正在复制图标文件...
    copy "%ICON_PATH%" "%TEMP_DIR%" > nul
) else (
    echo   - [警告] 未找到图标文件: %ICON_PATH%
)

echo.
echo [步骤 3/3] 正在调用 jpackage 生成安装程序...
echo   - 应用名称: %PROJECT_NAME%
echo   - 应用版本: %APP_VERSION%
echo   - 输出目录: %INSTALLER_DIR%

jpackage --type exe ^
    --dest "%INSTALLER_DIR%" ^
    --name "%PROJECT_NAME%" ^
    --app-version %APP_VERSION% ^
    --vendor "%VENDOR_NAME%" ^
    --win-console ^
    --input "%TEMP_DIR%" ^
    --main-jar %JAR_NAME% ^
    --main-class %MAIN_CLASS% ^
    --java-options "-Dfile.encoding=UTF-8" ^
    --win-shortcut ^
    --win-menu

if errorlevel 1 (
    echo.
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    echo [错误] jpackage 创建安装包失败！请检查上面的错误信息。
    echo 常见原因: 未安装 WiX Toolset v3.x 或 JAVA_HOME 环境变量未正确设置。
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    goto :end
)

echo.
echo =======================================================
echo      🎉 安装包生成成功! 🎉
echo   请在项目根目录的 %INSTALLER_DIR% 文件夹下查找 '%PROJECT_NAME%-%APP_VERSION%.exe'
echo =======================================================
echo.

rem Clean up
echo 正在清理临时文件...
rmdir /S /Q "%TEMP_DIR%"

:end
endlocal
echo.
echo 按任意键退出...
pause > nul 