# AI 搜题 APP

基于 Android + Kotlin + Jetpack Compose 开发的 AI 搜题应用，支持拍照识别题目并通过多模态大模型解答。

## 功能特性

- 📸 **图片识别**：支持相机拍照或相册选图
- 🤖 **AI 解答**：调用多模态大模型（支持 OpenAI 兼容接口）
- ⚙️ **灵活配置**：自定义 API Base URL、API Key 和模型名称
- 🎨 **现代 UI**：基于 Material Design 3 和 Jetpack Compose

## 技术栈

- **开发语言**：Kotlin
- **UI 框架**：Jetpack Compose + Material Design 3
- **架构模式**：MVVM + Repository
- **网络请求**：Retrofit + OkHttp
- **数据持久化**：DataStore Preferences
- **图片加载**：Coil
- **最低版本**：Android 7.0 (API 24)

## 项目结构

```
app/src/main/java/com/lilinth/questionsolver/
├── data/
│   ├── model/          # 数据模型
│   ├── repository/     # 数据仓库
│   └── api/            # API 接口定义
├── ui/
│   ├── screen/         # 屏幕页面
│   ├── viewmodel/      # ViewModel
│   └── theme/          # 主题配置
└── MainActivity.kt     # 主 Activity
```

## 开发计划

### MVP 版本 (v1.0.0)
- [x] 项目架构搭建
- [ ] 图片选择功能
- [ ] API 配置管理
- [ ] 多模态大模型调用
- [ ] 答案展示（纯文本）

### 后续版本
- [ ] 数学公式渲染（LaTeX/MathML）
- [ ] 历史记录功能
- [ ] 答案复制和分享
- [ ] 拍照功能（CameraX）
- [ ] 图片裁剪和旋转
- [ ] 深色/浅色主题切换

## 构建说明

1. 克隆项目
```bash
git clone https://github.com/Lilinth07/ai-question-solver.git
cd ai-question-solver
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 运行到设备或模拟器

## 使用说明

1. 首次打开应用，进入设置页面配置 API 信息
2. 填写 API Base URL、API Key 和模型名称
3. 返回主页，选择图片或拍照
4. 点击"开始解答"，等待 AI 返回答案

## API 兼容性

支持任何 OpenAI 兼容格式的 API，包括：
- OpenAI GPT-4V
- Azure OpenAI
- Claude (通过转换)
- 国内大模型平台（如智谱、百度等）

## License

MIT License
