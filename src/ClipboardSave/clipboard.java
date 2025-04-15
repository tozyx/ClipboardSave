package ClipboardSave;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class clipboard {
    private final Clipboard clipboard;

    public clipboard() {
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    //获取名字
    public String getName() {
        return clipboard.getName();
    }

    public String spawnName() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        return formatter.format(date)+".";
    }

    /**
     * 将字符串赋值到系统粘贴板
     *
     * @param data 要复制的字符串
     */
    public void setClipboardString(String data) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装data内容
        Transferable ts = new StringSelection(data);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(ts, null);
    }

    /**
     * 得到系统粘贴板上的String对象
     *
     * @return 内容
     */
    public String getClipboardString() {
        //获取系统粘贴板
        //Toolkit类：Abstract Window Toolkit的所有实际实现的抽象超类。 Toolkit类的子类用于将各种组件绑定到特定的本机Toolkit实现。
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //获取封装好的data数据
        Transferable ts = clipboard.getContents(null);
        if (ts != null) {
            // 判断剪贴板中的内容是否支持文本
            if (ts.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    return (String) ts.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 复制图像到系统粘贴板  （实际上我们不用手绘Image对象直接用File对象得到）
     *
     * @param path 图片的地址
     */
    private void copyImage(String path) {
        //将path得到的file转换成image
        Image image = null;
        File file = new File(path);
        BufferedImage bi;
        //通过io流操作把file对象转换成Image
        try {
            InputStream is = new FileInputStream(file);
            bi = ImageIO.read(is);
            image = (Image) bi;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //复制到粘贴板上
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //得到系统剪贴板
        Transferable selection = new ImageSelection(image);  //图像通道
        clipboard.setContents(selection, null);
    }

    /**
     * 得到系统粘贴板上的图片复制到我们项目路径下；
     *
     * @return 目标Image对象
     */

    //测试
    public Image getImageFromClipboard() {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clip.getContents(null);

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                // Handle file list flavors (for drag-and-drop support, though not common for clipboard)
                Object fileList = contents.getTransferData(DataFlavor.javaFileListFlavor);
                if (fileList instanceof java.util.List<?> files) {
                    if (!files.isEmpty() && files.get(0) instanceof File file) {
                        return ImageIO.read(file);
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                return (Image) contents.getTransferData(DataFlavor.imageFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

        // Optionally, handle other flavors like DataFlavor.stringFlavor for URLs or base64 encoded images

        return null; // Return null if no image found
    }

    public boolean writeImageToFile(Image image, String filePath, String formatName) {
        if (image == null) {
            System.err.println("Error: The provided Image object is null.");
            return false;
        }
        // 如果image不是BufferedImage的实例，我们需要先将其转换为BufferedImage
        if (!(image instanceof BufferedImage)) {
            // 创建一个与原始Image具有相同宽度和高度的BufferedImage
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            // 获取Graphics2D对象以在BufferedImage上绘制原始Image
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose(); // 释放Graphics2D资源

            // 使用ImageIO将BufferedImage写入文件
            return writeBufferedImageToFile(bufferedImage, filePath, formatName);
        } else {
            // 如果image已经是BufferedImage的实例，则直接写入文件
            return writeBufferedImageToFile((BufferedImage) image, filePath, formatName);
        }
    }

    private static boolean writeBufferedImageToFile(BufferedImage bufferedImage, String filePath, String formatName) {
        try {
            // 使用ImageIO将BufferedImage写入指定路径和格式的文件
            boolean result = ImageIO.write(bufferedImage, formatName, new File(filePath));
            return result; // 返回写入是否成功的布尔值
        } catch (IOException e) {
            e.printStackTrace();
            return false; // 如果发生IO异常，则返回false
        }
    }

    /**
     * 得到系统粘贴板上的图片复制到我们项目路径下；
     * @return 目标Image对象
     */
    public Image getImage() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable ts = clipboard.getContents(null);
        if(ts==null) return null;
        Image image = null;

        if(ts.isDataFlavorSupported(DataFlavor.imageFlavor)) {//这是判断是否支持粘贴图片
            try {
                image = (Image)ts.getTransferData(DataFlavor.imageFlavor);
                System.out.println("可以转换");
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

        //通过我们得到的image转换成BufferedImage对象来写出数据
        BufferedImage bi = (BufferedImage) image;
        bufferedImageToOutputStream(bi);

        return image;
    }

    /**
     * 通过file和BufferedImage 得到一个OutputStream对象来讲数据写入定义好的file对象
     * @param bufferedimage 我们从image转换来的
     */
    public void bufferedImageToOutputStream(BufferedImage bufferedimage){
        File file = new File("图片.png");
        if(file.exists()) file.delete();
        try {
            FileOutputStream os = new FileOutputStream(file);
            ImageIO.write(bufferedimage, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


