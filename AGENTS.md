# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

---

# AI 搜题 APP

Android 原生应用，通过拍照识别题目并调用多模态大模型进行解答。

**Package**: `com.lilinth.questionsolver`

## 开发命令

### 构建与运行
```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 安装到设备/模拟器
./gradlew installDebug

# 清理构建
./gradlew clean
```

### 测试
```bash
# 运行单元测试
./gradlew test

# 运行 UI 测试
./gradlew connectedAndroidTest

# 运行特定测试
./gradlew test --tests "com.lilinth.questionsolver.SpecificTest"
```

### 代码检查
```bash
# Lint 检查
./gradlew lint

# 查看 Lint 报告
./gradlew lintDebug
```

## 项目概述

**核心功能**：相册选图 → 图片 Base64 编码 → OpenAI 兼容接口调用 → 解析答案展示

**技术栈**：
- Kotlin 原生开发
- OpenAI 标准接口（支持任意兼容的多模态模型）
- Retrofit + OkHttp（网络请求）
- DataStore Preferences（配置持久化）
- Jetpack Compose（UI 框架）
- Coil（图片加载）
- ExifInterface（图片旋转处理）

**当前状态（MVP）**：
- ✅ 相册选图（通过 Photo Picker）
- ✅ API 配置管理（Base URL + API Key + Model）
- ✅ API 调用与答案展示
- ❌ 相机拍照（待实现）
- ❌ 历史记录（待实现）
- ❌ LaTeX 渲染（待实现）

## 架构设计

### MVVM + Repository 模式
- **UI 层**：Jetpack Compose（声明式 UI）
- **ViewModel 层**：处理 UI 逻辑和状态管理
- **Repository 层**：统一数据来源（API + 本地配置）
- **Data 层**：Retrofit API + DataStore（配置持久化）

### 实际模块结构
```
app/src/main/java/com/lilinth/questionsolver/
├── data/
│   ├── api/
│   │   ├── ApiService.kt           # Retrofit 接口定义
│   │   ├── RetrofitClient.kt       # 动态创建 Retrofit 实例
│   │   └── model/                  # API 数据模型（ChatRequest/ChatResponse）
│   ├── repository/
│   │   ├── QuestionRepository.kt   # 题目解答仓库
│   │   ├── ConfigRepository.kt     # 配置仓库
│   │   └── SettingsRepository.kt   
│   ├── model/                      # 数据模型（ApiConfig/ApiModels）
│   └── preferences/
│       └── PreferencesManager.kt   # DataStore 配置管理
├── ui/
│   ├── screen/
│   │   ├── MainScreen.kt           # 主界面（选图+解答）
│   │   └── SettingsScreen.kt       # 设置界面
│   ├── viewmodel/
│   │   ├── MainViewModel.kt        # 主界面 ViewModel
│   │   └── SettingsViewModel.kt    # 设置 ViewModel
│   ├── navigation/
│   │   └── Screen.kt               # 导航定义
│   └── theme/                      # Material 3 主题配置
├── util/
│   ├── ImageUtil.kt                # 图片压缩/Base64 编码
│   └── ImageUtils.kt               
└── MainActivity.kt                 # Compose 导航根节点
```

### 关键架构点

**动态 Retrofit 客户端**  
`RetrofitClient.createApiService(baseUrl, apiKey)` 根据用户配置动态创建 API 实例，支持任意 OpenAI 兼容端点。

**数据流**  
1. 用户在 `SettingsScreen` 配置 API（保存到 DataStore）
2. `MainScreen` 通过 Photo Picker 选择图片
3. `MainViewModel.solveQuestion()` 触发解答流程：
   - 读取图片 URI → 转换为 ByteArray
   - `ImageUtil` 压缩并 Base64 编码
   - `QuestionRepository` 构建 OpenAI Vision API 请求
   - `RetrofitClient` 发送请求
   - 解析 `response.choices[0].message.content`
   - 更新 UI 状态（答案/错误/加载中）

**配置管理**  
使用 DataStore Preferences 持久化：
- `base_url`: API 端点（如 `https://api.openai.com/`）
- `api_key`: 用户密钥
- `model_name`: 模型名称（如 `gpt-4-vision-preview`）

注意：`baseUrl` 必须以 `/` 结尾（Retrofit 要求）

## API 设计

### OpenAI 兼容接口格式
```kotlin
POST {base_url}/v1/chat/completions
Headers:
  Authorization: Bearer {api_key}
  Content-Type: application/json

Body:
{
  "model": "gpt-4-vision-preview",
  "messages": [
    {
      "role": "user",
      "content": [
        {
          "type": "text",
          "text": "请仔细分析这道题目，并给出详细的解答过程和最终答案。"
        },
        {
          "type": "image_url",
          "image_url": {
            "url": "data:image/jpeg;base64,..."
          }
        }
      ]
    }
  ]
}
```

**实现位置**：`QuestionRepository.solveQuestion()`

### 配置管理
- **base_url**：API 端点（必须以 `/` 结尾）
- **api_key**：用户自带密钥
- **model_name**：模型名称
- **存储方式**：DataStore Preferences（通过 `PreferencesManager`）

## 核心功能实现要点

### 图片处理（`util/ImageUtil.kt`）
- 从 URI 读取图片并转换为 ByteArray
- 使用 ExifInterface 处理 EXIF 旋转信息
- 压缩到合理大小（平衡清晰度和 API 传输）
- Base64 编码为 `data:image/jpeg;base64,...` 格式

### 答案展示（`MainScreen.kt`）
- 当前为纯文本展示（Material 3 Card）
- 支持加载状态（CircularProgressIndicator）
- 错误提示（errorContainer 配色）
- 状态管理通过 StateFlow

### 配置验证
`ApiConfig.isValid()` 检查：
- baseUrl 非空且以 `/` 结尾
- apiKey 非空
- modelName 非空

### 权限处理
- **READ_MEDIA_IMAGES**（Android 13+）
- **READ_EXTERNAL_STORAGE**（Android 12 及以下）
- Photo Picker 自动处理权限请求（`ActivityResultContracts.PickVisualMedia`）

## 技术选型细节

### Android 版本
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 35 (Android 15)
- **compileSdk**: 35

### 核心依赖（Version Catalog）
使用 `libs.versions.toml` 管理依赖版本。

关键库：
- Jetpack Compose BOM
- Retrofit 2.9.0 + Gson Converter
- OkHttp Logging Interceptor
- Coil Compose（图片加载）
- DataStore Preferences
- ExifInterface 1.3.7（处理图片旋转）

### Gradle 配置
- **JVM Target**: Java 11
- **Kotlin**: 使用官方代码风格（`kotlin.code.style=official`）
- **构建工具**: Gradle 8.9 + Wrapper

## 开发规范

### 命名约定
- Activity: `XxxActivity`
- ViewModel: `XxxViewModel`
- Repository: `XxxRepository`
- API 接口: `XxxService`
- Compose Screen: `XxxScreen`

### 代码风格
- 遵循 Kotlin 官方编码规范
- 使用协程处理异步操作（`suspend` 函数）
- MVVM 架构模式
- StateFlow 管理 UI 状态
- 单一职责原则

### Git 提交规范
- `feat`: 新功能
- `fix`: 修复
- `refactor`: 重构
- `docs`: 文档
- `style`: 格式调整

## CI/CD

项目使用 GitHub Actions 自动构建（`.github/workflows/build.yml`）：
- 推送到 `master` 分支时触发
- 构建 Debug APK 并上传为 Artifact
- 打 tag 时自动创建 Release

## 后续扩展方向

### 计划中的功能
- [ ] 相机拍照（CameraX）
- [ ] 历史记录（Room 数据库）
- [ ] LaTeX 公式渲染（WebView + KaTeX 或原生库）
- [ ] Markdown 富文本展示
- [ ] 答案复制功能
- [ ] 深色/浅色主题切换
- [ ] 答案缓存（相同题目复用结果）
- [ ] 多轮对话（追问）
- [ ] 图片裁剪功能

### 长期规划
- [ ] 支持 iOS（考虑 Kotlin Multiplatform）
- [ ] 公式编辑器（手动输入题目）
- [ ] 错题本功能
- [ ] 学科分类（数学、物理、化学等）
- [ ] 离线 OCR（先识别文字再调用 API，降低成本）

## 安全性注意事项

- API Key 存储在 DataStore（考虑使用 Android Keystore 加密）
- 图片不上传服务器（直接 base64 传给 API）
- 网络请求使用 HTTPS（`usesCleartextTraffic` 仅用于开发）
- 敏感信息不写入日志（生产环境应关闭 HttpLoggingInterceptor）
