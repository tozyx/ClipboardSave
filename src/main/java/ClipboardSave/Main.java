package ClipboardSave;

import java.awt.*;


public class Main {
    // 将格式定义为常量或配置
    public static final String FILE_FORMAT = "png";

    public static void main(String[] args) {
        // 1. 自检：生成脚本和注册表文件 (仅需执行一次)
        SetupManager.checkAndCreateScripts();

        // 2. 判断是否是从右键菜单（带参数）启动
        if (args.length > 0) {
            String targetPath = args[0];
            try {
                ClipboardUtils utils = new ClipboardUtils();
                Image img = utils.getImageFromClipboard();

                if (img != null) {
                    String fileName = "剪贴板_" + utils.spawnName() + ".png";
                    // 执行保存逻辑
                    utils.writeImageToFile(img, targetPath + "\\" + fileName, "png");

                    // 3. 成功后发送系统通知
                    NotificationUtils.showNativeNotification("图片保存成功", "已存至: " + fileName);
                } else {
                    NotificationUtils.showNativeNotification("提示", "剪贴板里没有图片哦");
                }
            } catch (Exception e) {
                NotificationUtils.showNativeNotification("错误", "保存失败: " + e.getMessage());
            }
        } else {
            // 4. 无参数运行时（用户直接双击 JAR）
            System.out.println("请右键点击文件夹背景运行此程序。");
            NotificationUtils.showNativeNotification("提示", "先添加注册表！\n请右键点击文件夹背景运行此程序");
            // 也可以在这里加个弹窗提示用户
        }
    }
}