# Android SDK 配置说明

## 问题
构建时出现错误：
```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable 
or by setting the sdk.dir path in your project's local properties file
```

## 解决方案

### 方案 1：设置 ANDROID_HOME 环境变量（推荐）

1. 找到你的 Android SDK 安装路径，通常在：
   - Windows: `C:\Users\你的用户名\AppData\Local\Android\Sdk`
   - 或 Android Studio 安装目录下

2. 设置环境变量：
   ```powershell
   # 临时设置（当前会话）
   $env:ANDROID_HOME = "C:\Users\你的用户名\AppData\Local\Android\Sdk"
   
   # 永久设置（系统环境变量）
   [System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\你的用户名\AppData\Local\Android\Sdk", "User")
   ```

3. 重启终端后构建：
   ```bash
   .\gradlew.bat assembleDebug
   ```

### 方案 2：创建 local.properties 文件

在项目根目录创建 `local.properties` 文件：

```properties
# 替换为你的实际 SDK 路径（使用正斜杠或双反斜杠）
sdk.dir=C:/Users/你的用户名/AppData/Local/Android/Sdk
# 或
# sdk.dir=C:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

**注意**：`local.properties` 已在 `.gitignore` 中，不会提交到 Git。

### 方案 3：使用 Android Studio

1. 在 Android Studio 中打开项目
2. Android Studio 会自动检测并配置 SDK
3. 或在 `File > Project Structure > SDK Location` 中手动设置

## 验证配置

```powershell
# 检查环境变量
$env:ANDROID_HOME

# 应该输出类似：
# C:\Users\Administrator\AppData\Local\Android\Sdk
```

## 构建项目

配置完成后：

```bash
# 清理并构建
.\gradlew.bat clean assembleDebug

# 安装到设备
.\gradlew.bat installDebug
```

## 查找 Android SDK 路径

如果不确定 SDK 安装在哪里：

```powershell
# 方法 1：通过 Android Studio
# File > Settings > Appearance & Behavior > System Settings > Android SDK

# 方法 2：常见位置
Test-Path "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"

# 方法 3：搜索 adb.exe
Get-ChildItem -Path C:\ -Recurse -Filter adb.exe -ErrorAction SilentlyContinue | Select-Object -First 1 Directory
```
