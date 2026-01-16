# ClipboardSave 📋

[English](#english) | [中文](#中文)

---

## 中文

一个轻量级的 Windows 右键菜单工具，只需点击一下即可将剪贴板中的图片保存到当前文件夹。

### ✨ 功能特点
- **原生集成**：直接集成到 Windows 右键菜单（文件夹背景及目录）。
- **极速保存**：自动以时间戳命名（格式：`剪贴板_yyyy-MM-dd-HHmmss.png`）。
- **原生通知**：基于 JNA 调用 Windows API，显示带自定义图标的系统通知。
- **静默运行**：使用 VBS 脚本调用，运行过程中无黑窗口闪烁。
- **环境自愈**：首次运行自动释放图标资源并生成注册表修复文件。

### 🚀 快速开始

#### 1. 构建项目([已经提供构建好的工件，点击下载](https://github.com/tozyx/ClipboardSave/releases))
确保你已安装 JDK 17+ 和 Gradle。在项目根目录运行：
```bash
./gradlew jar
```
构建出的 Fat JAR 将位于 build/libs/ClipboardSave-1.0-SNAPSHOT.jar。
#### 2. 安装步骤
1. 将生成的 JAR 文件移动到一个固定的位置（存放后请勿移动，否则右键菜单会失效）。
2. 双击运行该 JAR 文件，它会自动在同级目录下生成以下文件：
    - clipboardsave.ico (图标资源)
    - run_silent.vbs (后台静默启动脚本)
    - install_menu.reg (右键菜单注册表安装文件)
    - uninstall_menu.reg (右键菜单卸载文件)
3. 双击运行 install_menu.reg 并点击“是”确认导入注册表。

#### 3. 使用方法
在任意文件夹空白处点击右键，选择菜单中的 "保存剪贴板图片" 即可。

---

## English

A lightweight Windows context menu utility that saves images from your clipboard to the current folder with a single click.

### ✨ Features
- Native Integration: Seamlessly integrates into Windows Context Menu (Background & Directory).
- Instant Save: Auto-naming with timestamps (Format: 剪贴板_yyyy-MM-dd-HHmmss.png).
- Native Notifications: Uses JNA to call Windows APIs for system-style toast notifications with custom icons.
- Silent Execution: Invoked via VBScript to prevent any command prompt window flickers.
- Self-Healing: Automatically extracts icon resources and generates registry fix files on the first run.

### 🚀 Quick Start

#### 1. Build([Pre-built artifacts available, click to download]( https://github.com/tozyx/ClipboardSave/releases))
Requires JDK 17+ and Gradle. Run the following command in the root directory: ./gradlew jar
```bash
./gradlew jar
```
The Fat JAR will be located at build/libs/ClipboardSave-1.0-SNAPSHOT.jar.

#### 2. Installation
1. Move the JAR file to a permanent location (do not move it after setup, or the context menu will break).
2. Double-click the JAR to generate necessary files:
    - clipboardsave.ico (Icon resource)
    - run_silent.vbs (Silent startup script)
    - install_menu.reg (Registry installer)
    - uninstall_menu.reg (Registry uninstaller)
3. Double-click install_menu.reg and confirm the import.

#### 3. Usage
Right-click on any folder background and select "Save Clipboard Image".

---

### 🛠 Tech Stack
- Java 17
- Gradle (Build Tool)
- JNA (Java Native Access): For Windows Shell_NotifyIcon and User32 API calls.

---

### 📄 License
This project is for personal use and educational purposes.