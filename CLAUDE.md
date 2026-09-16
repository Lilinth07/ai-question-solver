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
- Jetpack Compose / XML（UI，待确认）

## 架构设计

### 模块划分
```
app/
├── data/              # 数据层
│   ├── api/          # API 接口定义
│   ├── model/        # 数据模型
│   ├── repository/   # 数据仓库
│   └── local/        # 本地存储
├── domain/           # 业务逻辑层（可选）
├── ui/               # UI 层
│   ├── camera/       # 拍照模块
│   ├── answer/       # 答案展示
│   ├── history/      # 历史记录
│   └── settings/     # 设置（API 配置）
└── util/             # 工具类
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

### 3. 历史记录
- 本地 Room 数据库
- 字段：题目图片路径、答案文本、时间戳、模型名称
- 支持搜索和删除

### 4. 用户体验
- 加载动画（API 调用需要几秒）
- 错误处理：网络超时、API 密钥无效、余额不足、图片不清晰等
- 离线提示
- 暗色模式适配

## 依赖库

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.9.20"
    
    // 网络
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"
    
    // 图片
    implementation "com.github.bumptech.glide:glide:4.16.0"
    
    // 相机
    implementation "androidx.camera:camera-camera2:1.3.0"
    implementation "androidx.camera:camera-lifecycle:1.3.0"
    implementation "androidx.camera:camera-view:1.3.0"
    
    // 数据库
    implementation "androidx.room:room-runtime:2.6.0"
    implementation "androidx.room:room-ktx:2.6.0"
    kapt "androidx.room:room-compiler:2.6.0"
    
    // Jetpack
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.6.2"
    
    // Markdown 渲染
    implementation "io.noties.markwon:core:4.6.2"
    implementation "io.noties.markwon:ext-latex:4.6.2"
}
```

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

## 开发规范

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
