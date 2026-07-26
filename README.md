# MCLauncher

MCLauncher 是一款自研的《我的世界》Java版 Android 第三方启动器，支持版本下载、模组管理、整合包、资源包、光影和存档管理。

## 功能特性

- **版本管理**: 支持从 Mojang 官方下载和管理 Minecraft Java Edition 多个版本
- **模组下载**: 支持从 CurseForge、Modrinth、Planet Minecraft 浏览和下载模组
- **整合包**: 支持浏览和安装整合包
- **资源包**: 浏览和下载纹理、字体等资源包
- **光影**: 浏览和下载光影包（Shader）
- **存档**: 浏览和下载世界/地图存档
- **黑白主题**: 内置深色和浅色两种主题，可一键切换
- **正版/离线登录**: 支持多种登录方式

## 内容平台

| 平台 | 支持状态 |
|------|---------|
| CurseForge | 已支持 |
| Modrinth | 已支持 |
| Planet Minecraft | 已支持 |

## 系统要求

- Android 8.0 (API 26) 及以上
- 至少 2GB RAM
- 建议预留 2GB 以上存储空间用于安装游戏文件

## 安装

下载 `MCLauncher-v1.0.0.apk` 并安装到 Android 设备。

## 技术架构

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material 3
- **网络**: Retrofit + OkHttp
- **构建工具**: Gradle (Kotlin DSL)
- **最低 SDK**: 26 (Android 8.0)
- **目标 SDK**: 34 (Android 14)

## 项目结构

```
MCLauncher/
├── app/
│   └── src/main/java/com/mclauncher/
│       ├── MainActivity.kt          # 主 Activity
│       ├── MCApplication.kt         # Application
│       ├── launcher/                # 启动器核心
│       │   ├── MinecraftLauncher.kt # 游戏启动
│       │   └── VersionDownloader.kt # 版本下载
│       ├── network/                 # 网络 API
│       │   ├── ApiClient.kt         # API 客户端
│       │   ├── CurseForgeApi.kt     # CurseForge API
│       │   ├── ModrinthApi.kt       # Modrinth API
│       │   └── MinecraftApi.kt      # Minecraft API
│       ├── data/models/             # 数据模型
│       └── ui/                      # UI 层
│           ├── theme/               # 主题系统
│           ├── screens/             # 页面
│           ├── navigation/          # 导航
│           └── components/          # 组件
└── gradle/
```

## 构建

```bash
# 克隆项目
git clone <repo-url>

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

## 许可证

MIT License
