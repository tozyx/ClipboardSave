package ClipboardSave;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;import java.nio.file.StandardCopyOption;

public class SetupManager {

    public static void checkAndCreateScripts() {
        try {
            String jarPath = Paths.get(SetupManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
            String currentDir = new File(jarPath).getParent();

            // 定义文件路径
            Path icoPath = Paths.get(currentDir, "clipboardsave.ico");
            Path pngPath = Paths.get(currentDir, "clipboardsave.png");
            Path vbsPath = Paths.get(currentDir, "run_silent.vbs");
            Path regPath = Paths.get(currentDir, "首次运行先添加注册表.reg");
            Path uninstallRegPath = Paths.get(currentDir, "删除对应注册表.reg");

            exportResource("/clipboardsave.ico", icoPath);
            exportResource("/clipboardsave.png", pngPath);

            // 生成 VBS 脚本
            if (Files.notExists(vbsPath)) {
                String vbsContent = "CreateObject(\"Wscript.Shell\").Run \"java -jar \"\""
                        + jarPath + "\"\" \"\"\" & WScript.Arguments(0) & \"\"\"\", 0, False";
                Files.write(vbsPath, vbsContent.getBytes("GBK"));
                System.out.println("已生成静默启动脚本: " + vbsPath);
            }

            // 3. 生成 REG (关键：将 Icon 指向释放出的 icoPath)
            if (Files.notExists(regPath)) {
                String regContent = getString(icoPath, vbsPath);
                System.out.println("已生成注册表文件，请双击运行: " + regPath);
                Files.write(regPath, regContent.getBytes("GBK"));
            }

            //4. 生成 REG 删除对应注册表
            if (Files.notExists(uninstallRegPath)) {
                String uninstallContent = "Windows Registry Editor Version 5.00\n\n" +
                        "[-HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\SaveClipboardImage]\n\n" +
                        "[-HKEY_CLASSES_ROOT\\Directory\\shell\\SaveClipboardImage]";
                Files.write(uninstallRegPath, uninstallContent.getBytes("GBK"));
                System.out.println("已生成卸载注册表文件: " + uninstallRegPath);
            }

        } catch (Exception e) {
            System.err.println("初始化环境失败: " + e.getMessage());
        }
    }


    private static String getString(Path icoPath, Path vbsPath) {
        String escapedIcoPath = icoPath.toString().replace("\\", "\\\\");
        String escapedVbsPath = vbsPath.toString().replace("\\", "\\\\");

        return "Windows Registry Editor Version 5.00\n\n" +
                "[HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\SaveClipboardImage]\n" +
                "@=\"保存剪贴板图片到此处\"\n" +
                "\"Icon\"=\"" + escapedIcoPath + "\"\n\n" +
                "[HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\SaveClipboardImage\\command]\n" +
                "@=\"wscript.exe \\\"" + escapedVbsPath + "\\\" \\\"%V\\\"\"\n\n" +
                "[HKEY_CLASSES_ROOT\\Directory\\shell\\SaveClipboardImage]\n" +
                "@=\"保存剪贴板图片到此处\"\n" +
                "\"Icon\"=\"" + escapedIcoPath + "\"\n\n" +
                "[HKEY_CLASSES_ROOT\\Directory\\shell\\SaveClipboardImage\\command]\n" +
                "@=\"wscript.exe \\\"" + escapedVbsPath + "\\\" \\\"%1\\\"\"";
    }


    //释放资源
    private static void exportResource(String resourcePath, Path targetPath) throws IOException {
        // 如果外部文件不存在，则从 JAR 内部复制出来
        if (Files.notExists(targetPath)) {
            try (InputStream is = SetupManager.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IOException("在 JAR 根目录找不到资源: " + resourcePath);
                }
                Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("成功释放资源文件: " + targetPath.getFileName());
            }
        }
    }
}