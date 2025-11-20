package fr.maxlego08.zauctionhouse.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author sya-ri
 * Github: <a href="https://github.com/sya-ri/base64-itemstack/tree/master">https://github.com/sya-ri/base64-itemstack/tree/master</a>
 */
public class Base64ItemStack {

    public static String encode(ItemStack item) {
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            ObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(gzipOutputStream);
            objectOutputStream.writeObject(item);
            objectOutputStream.close();
            return encoder.encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static ItemStack decode(String data) {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] bytes = decoder.decode(data);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ObjectInputStream objectInputStream = new BukkitObjectInputStream(gzipInputStream);
            ItemStack item = (ItemStack) objectInputStream.readObject();
            objectInputStream.close();
            return item;
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}