# AI 搜题 APP

Android 原生应用，通过拍照识别题目并调用多模态大模型进行解答。

## 项目概述

**核心功能**：拍照/相册选图 → 图片处理 → OpenAI 兼容接口调用 → 解析答案展示

**技术栈**：
- Kotlin 原生开发
- OpenAI 标准接口（支持任意兼容的多模态模型）
- CameraX（相机调用）
- Retrofit + OkHttp（网络请求）
- Room（本地数据库）
- Jetpack Compose（UI 框架）

**MVP 范围**：
- 拍照/相册选图
- API 调用与答案展示
- 基础设置（API 配置）
- 暂不包含：历史记录、LaTeX 渲染、离线缓存

## 架构设计

### MVP 架构
采用 MVVM + Repository 模式：
- **UI 层**：Jetpack Compose（声明式 UI，减少样板代码）
- **ViewModel 层**：处理 UI 逻辑和状态管理
- **Repository 层**：统一数据来源（API + 本地配置）
- **Data 层**：Retrofit API + DataStore（配置持久化）

### 模块划分（MVP 精简版）
```
app/
├── data/
│   ├── api/
│   │   ├── OpenAIApi.kt              # Retrofit 接口定义
│   │   └── model/                     # API 请求/响应模型
│   ├── repository/
│   │   ├── QuestionRepository.kt     # 题目解答仓库
│   │   └── SettingsRepository.kt     # 配置仓库
│   └── local/
│       └── UserPreferences.kt        # DataStore 配置
├── ui/
│   ├── camera/
│   │   ├── CameraScreen.kt           # 拍照界面
│   │   └── CameraViewModel.kt
│   ├── answer/
│   │   ├── AnswerScreen.kt           # 答案展示
│   │   └── AnswerViewModel.kt
│   └── settings/
│       ├── SettingsScreen.kt         # API 配置
│       └── SettingsViewModel.kt
├── util/
│   ├── ImageProcessor.kt             # 图片压缩/Base64
│   └── NetworkResult.kt              # 网络结果封装
└── MainActivity.kt                    # Compose 导航
```

### 数据流
1. **拍照/选图** → ImageProcessor 压缩裁剪
2. **编码** → Base64 + data URI scheme
3. **构建请求** → OpenAI Vision API 格式
4. **发送** → Retrofit 动态 base URL + API Key
5. **解析** → 提取答案文本，LaTeX 公式渲染
6. **存储** → Room 保存历史记录

## API 设计

### OpenAI 兼容接口
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
      "role": "system",
      "content": "你是一个专业的解题助手..."
    },
    {
      "role": "user",
      "content": [
        {
          "type": "text",
          "text": "请解答这道题目"
        },
        {
          "type": "image_url",
          "image_url": {
            "url": "data:image/jpeg;base64,..."
          }
        }
      ]
    }
  ],
  "max_tokens": 1000
}
```

### 配置存储
- **base_url**：API 端点（默认 `https://api.openai.com`）
- **api_key**：用户自带密钥
- **model**：模型名称（`gpt-4-vision-preview`, `claude-3-opus-20240229` 等）
- 使用 SharedPreferences 或 DataStore 持久化

## 核心功能实现要点

### 1. 图片处理
- 压缩到 ≤ 2MB（平衡清晰度和 API 成本）
- 支持旋转校正
- 可选裁剪框（提高识别准确率）
- Base64 编码

### 2. 答案展示
- Markdown 渲染（支持代码块、列表等）
- LaTeX 公式渲染（WebView + MathJax/KaTeX 或原生库）
- 步骤分解展示
- 支持复制答案

### 3. 用户体验（MVP 简化版）
- 加载动画（API 调用需要几秒）
- 错误处理：网络超时、API 密钥无效、余额不足等
- 离线提示
- 暗色模式适配

## MVP 开发计划

## 技术选型细节

### 最低 Android 版本
- **minSdk**: 24 (Android 7.0) - 覆盖 95%+ 设备
- **targetSdk**: 34 (Android 14)
- **compileSdk**: 34

### 核心依赖库（MVP 版本）

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.9.22"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    
    // Jetpack Compose
    implementation platform("androidx.compose:compose-bom:2024.02.00")
    implementation "androidx.compose.ui:ui"
    implementation "androidx.compose.ui:ui-tooling-preview"
    implementation "androidx.compose.material3:material3"
    implementation "androidx.activity:activity-compose:1.8.2"
    implementation "androidx.navigation:navigation-compose:2.7.6"
    
    // ViewModel & Lifecycle
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
    implementation "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
    
    // 网络
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"
    
    // 相机
    implementation "androidx.camera:camera-camera2:1.3.1"
    implementation "androidx.camera:camera-lifecycle:1.3.1"
    implementation "androidx.camera:camera-view:1.3.1"
    
    // 图片处理
    implementation "io.coil-kt:coil-compose:2.5.0"
    
    // DataStore（配置存储）
    implementation "androidx.datastore:datastore-preferences:1.0.0"
    
    // 权限请求
    implementation "com.google.accompanist:accompanist-permissions:0.34.0"
}
```

### 暂不引入（后续版本）
- Room 数据库（MVP 不做历史记录）
- Markdown/LaTeX 渲染（MVP 纯文本展示）
- Hilt/Koin 依赖注入（项目规模小，手动注入即可）

## Prompt 策略

系统提示词模板：
```
你是一个专业的解题助手。请分析图片中的题目并给出详细解答。

要求：
1. 首先识别并复述题目内容
2. 给出详细的解题步骤
3. 解释每一步的原理和依据
4. 数学公式使用 LaTeX 格式（用 $ 或 $$ 包裹）
5. 最终答案用【答案】标记

如果图片模糊或无法识别题目，请明确说明。
```

## MVP 开发计划

### 第一阶段：项目初始化
- [x] Git 仓库初始化
- [x] 技术架构确定
- [ ] Android 项目骨架（build.gradle、AndroidManifest）
- [ ] Compose 导航结构

### 第二阶段：核心功能
- [ ] 设置页面（API 配置界面 + DataStore 存储）
- [ ] 相机模块（CameraX 拍照 + 相册选图）
- [ ] 图片处理（压缩 + Base64 编码）
- [ ] API 调用（Retrofit + OpenAI 接口）
- [ ] 答案展示（纯文本 + 复制功能）

### 第三阶段：完善体验
- [ ] 加载状态与错误处理
- [ ] 暗色模式适配
- [ ] 权限请求优化
- [ ] 基础 UI 打磨

### MVP 不包含的功能（v2.0 规划）
- 历史记录（Room 数据库）
- LaTeX 公式渲染
- Markdown 富文本
- 答案缓存
- 多轮对话

### 命名约定
- Activity/Fragment：`XxxActivity`, `XxxFragment`
- ViewModel：`XxxViewModel`
- Repository：`XxxRepository`
- API 接口：`XxxApi`

### 代码风格
- 遵循 Kotlin 官方编码规范
- 使用协程处理异步操作
- MVVM 架构模式
- 单一职责原则

### Git 提交
- feat: 新功能
- fix: 修复
- refactor: 重构
- docs: 文档
- style: 格式调整

## 后续扩展方向

- [ ] 支持 iOS（考虑 Kotlin Multiplatform）
- [ ] 答案缓存（相同题目复用结果）
- [ ] 多轮对话（追问）
- [ ] 公式编辑器（手动输入题目）
- [ ] 错题本功能
- [ ] 学科分类（数学、物理、化学等）
- [ ] 离线 OCR（先识别文字再调用 API，降低成本）

## 安全性考虑

- API Key 加密存储（考虑 Android Keystore）
- 图片不上传服务器（直接 base64 传给 API）
- 历史记录支持删除
- 网络请求证书校验
