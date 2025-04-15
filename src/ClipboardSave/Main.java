package ClipboardSave;


import java.io.File;
import java.util.Arrays;

public class Main {
    public static String format = "png";

    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.println("Received directory: " + Arrays.toString(args).replaceAll("^\\[|]$", ""));
            clipboard cp = new clipboard();
            cp.writeImageToFile(cp.getImageFromClipboard(),  Arrays.toString(args).replaceAll("^\\[|]$", "")+"\\剪贴板"+cp.spawnName() + format, format);
        } else {
            System.out.println("No directory path provided!");
        }
        clipboard cp = new clipboard();
        cp.getImage();

    }
}