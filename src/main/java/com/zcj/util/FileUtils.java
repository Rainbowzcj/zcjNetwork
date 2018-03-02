package com.zcj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public abstract class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 创建文件
     */
    public static boolean createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            logger.info("创建文件 {} 失败，目标文件已存在", filePath);
            return false;
        }

        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            logger.info("文件目录不存在，创建目录 {} 失败", dir);
            return false;
        }

        return file.createNewFile();
    }

    /**
     * 创建目录
     */
    public static boolean createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            logger.info("创建目录 {} 失败，目标目录已经存在", dirPath);
            return false;
        }

        //创建目录
        if (dir.mkdirs()) {
            logger.info("创建目录 {} 成功", dirPath);
            return true;
        } else {
            logger.info("创建目录 {} 失败", dirPath);
            return false;
        }
    }

    /**
     * 得到指定文件路径下的所有文件
     *
     * @param path 目录路径
     * @return
     */
    public static List<File> getFiles(String path) {
        return getFiles(new File(path));
    }

    /**
     * 递归得到目录下所有文件
     *
     * @param root 根目录
     * @return
     */
    public static List<File> getFiles(File root) {
        List<File> files = new ArrayList<>();
        if (!root.isDirectory()) {
            files.add(root);
        } else {
            File[] subFiles = root.listFiles();
            for (File subFile : subFiles) {
                files.addAll(getFiles(subFile));
            }
        }

        return files;
    }


    /**
     * 删除目录下所有文件
     */
    public static boolean deleteDir(File root) {
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (File child : children) {
                if (!deleteDir(child)) {
                    return false;
                }
            }
        }

        return root.delete();
    }

    /** 获得文件的扩展名 */
    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /** 判断文件名是否是图片格式 */
    public static boolean isImage(String fileName) {
        String ext = getExtension(fileName);
        return ext.equalsIgnoreCase("jpg")
            || ext.equalsIgnoreCase("png")
            || ext.equalsIgnoreCase("jpeg");
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr   URL地址
     * @param fileName 文件名
     * @param destPath 目标目录路径
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String destPath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //文件保存位置
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        File file = new File(destPath + File.separator + fileName);

        //得到URL输入流和文件输出流
        try (InputStream inputStream = conn.getInputStream();
             BufferedInputStream bis = new BufferedInputStream(inputStream);
             OutputStream fos = new FileOutputStream(file)) {
            //读取输入流写入输出流
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        }
        logger.info("{} download success", urlStr);
    }
}
