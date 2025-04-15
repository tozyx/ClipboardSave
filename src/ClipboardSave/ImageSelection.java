package ClipboardSave;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ImageSelection implements Transferable{
    private Image image;
    // 构造器，负责持有一个Image对象；
    public ImageSelection(Image image) {
        this.image = image;
    }

    // 返回该 Transferable对象所支持的所有DataFlavor
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        // TODO Auto-generated method stub
        return new DataFlavor[] { DataFlavor.imageFlavor};
    }

    // 返回该Transferable对象是否支持指定的DataFlavor
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        // TODO Auto-generated method stub
        return flavor.equals(DataFlavor.imageFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        // TODO Auto-generated method stub
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }


}

