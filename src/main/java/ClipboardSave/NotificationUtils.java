package ClipboardSave;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class NotificationUtils {

    // --- 1. 定义 Shell32 接口 ---
    public interface Shell32 extends StdCallLibrary {
        Shell32 INSTANCE = Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean Shell_NotifyIcon(int dwMessage, NOTIFYICONDATA lpData);
    }

    // --- 2. 定义 User32 接口 ---
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        // 核心修正：显式声明 LoadImageW 方法
        WinNT.HANDLE LoadImageW(
                HINSTANCE hInst,
                String name,
                int type,   // IMAGE_ICON = 1
                int cx,
                int cy,
                int fuLoad  // LR_LOADFROMFILE = 0x10 | LR_DEFAULTSIZE = 0x40
        );
    }

    // --- 3. 定义 NOTIFYICONDATA 结构体 ---
    @Structure.FieldOrder({"cbSize", "hWnd", "uID", "uFlags", "uCallbackMessage", "hIcon", "szTip", "dwState", "dwStateMask", "szInfo", "uTimeoutOrVersion", "szInfoTitle", "dwInfoFlags"})
    public static class NOTIFYICONDATA extends Structure {
        public int cbSize;
        public HWND hWnd;
        public int uID;
        public int uFlags;
        public int uCallbackMessage;
        public Pointer hIcon;
        public char[] szTip = new char[128];
        public int dwState;
        public int dwStateMask;
        public char[] szInfo = new char[256];
        public int uTimeoutOrVersion;
        public char[] szInfoTitle = new char[64];
        public int dwInfoFlags;

        public NOTIFYICONDATA() {
            // 核心修正：显式初始化数组，防止 JNA 报错
            szTip = new char[128];
            szInfo = new char[256];
            szInfoTitle = new char[64];
            cbSize = size();
        }
    }

    /**
     * 发送原生 Windows 通知
     * @param title 弹窗加粗标题
     * @param message 弹窗详细内容
     */
    public static void showNativeNotification(String title, String message) {
        try {
            NOTIFYICONDATA data = new NOTIFYICONDATA();
            data.uID = (int) (System.currentTimeMillis() % 100000);

            // 1. 获取本地图标路径
            String jarPath = Paths.get(NotificationUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
            String currentDir = new File(jarPath).getParent();
            String iconPath = new File(currentDir, "clipboardsave.ico").getAbsolutePath();

            // 2. 加载图标句柄 (IMAGE_ICON = 1, LR_LOADFROMFILE = 0x10, LR_DEFAULTSIZE = 0x40)
            WinNT.HANDLE hIconHandle = User32.INSTANCE.LoadImageW(null, iconPath, 1, 0, 0, 0x00000010 | 0x00000040);

            if (hIconHandle != null) {
                // NIF_INFO (0x10) | NIF_ICON (0x02)
                data.uFlags = 0x00000010 | 0x00000002;
                data.hIcon = hIconHandle.getPointer();
                // NIIF_USER (0x04) 表示在弹窗中使用自定义图标
                data.dwInfoFlags = 0x00000004;
            } else {
                data.uFlags = 0x00000010;
                data.dwInfoFlags = 0x00000001; // 回退到系统 NIIF_INFO 图标
            }

            // 3. 填充标题和内容
            char[] titleChars = title.toCharArray();
            System.arraycopy(titleChars, 0, data.szInfoTitle, 0, Math.min(titleChars.length, 63));
            char[] msgChars = message.toCharArray();
            System.arraycopy(msgChars, 0, data.szInfo, 0, Math.min(msgChars.length, 255));

            // 4. 显示通知
            // 先添加图标 (NIM_ADD = 0) 再修改触发显示 (NIM_MODIFY = 1)
            Shell32.INSTANCE.Shell_NotifyIcon(0, data);
            Shell32.INSTANCE.Shell_NotifyIcon(1, data);

            // 5. 阻塞主线程 3 秒，防止进程过早退出导致通知消失
            Thread.sleep(3000);

            // 6. 清理托盘图标
            Shell32.INSTANCE.Shell_NotifyIcon(2, data); // NIM_DELETE

        } catch (Exception e) {
            System.err.println("原生通知发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}