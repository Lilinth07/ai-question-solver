# AI 搜题 APP

Android 原生应用，通过拍照识别题目并调用多模态大模型进行解答。

## 功能特性

- 📷 拍照或从相册选择题目图片
- 🤖 支持任意 OpenAI 兼容接口的多模态模型
- 🔧 自定义 API Key 和 Base URL
- 📝 详细的解题步骤展示
- 🧮 LaTeX 数学公式渲染
- 📚 本地历史记录
- 🌙 暗色模式支持

## 技术栈

- **语言**：Kotlin
- **架构**：MVVM
- **网络**：Retrofit + OkHttp
- **数据库**：Room
- **相机**：CameraX
- **图片处理**：Glide

## 快速开始

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK API 24+（最低支持 Android 7.0）

### 构建项目

```bash
git clone https://github.com/your-username/ai-question-solver.git
cd ai-question-solver
./gradlew build
```

### 配置 API

首次启动应用后，进入设置页面配置：

- **Base URL**：API 端点地址（例如 `https://api.openai.com`）
- **API Key**：你的 API 密钥
- **Model**：模型名称（例如 `gpt-4-vision-preview`）

支持的模型提供商：
- OpenAI (GPT-4V)
- Anthropic Claude (通过兼容接口)
- Google Gemini Pro Vision
- 国内厂商（通义千问、文心一言等）

## 项目结构

```
app/
├── data/              # 数据层
│   ├── api/          # API 接口定义
│   ├── model/        # 数据模型
│   ├── repository/   # 数据仓库
│   └── local/        # 本地存储
├── ui/               # UI 层
│   ├── camera/       # 拍照模块
│   ├── answer/       # 答案展示
│   ├── history/      # 历史记录
│   └── settings/     # 设置
└── util/             # 工具类
```

## 开发计划

- [x] 项目初始化
- [ ] API 调用模块
- [ ] 相机功能
- [ ] 图片处理
- [ ] 答案展示
- [ ] 历史记录
- [ ] 设置页面
- [ ] LaTeX 渲染
- [ ] 单元测试

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue。
