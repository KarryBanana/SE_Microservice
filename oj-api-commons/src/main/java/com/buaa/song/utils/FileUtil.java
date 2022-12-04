package com.buaa.song.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @FileName: FileUtil
 * @author: ProgrammerZhao
 * @Date: 2020/12/15
 * @Description:
 */

public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static boolean createDir(String path) {
        File dir = new File(path);
        boolean b = true;
        if (!dir.exists()) {
            b = dir.mkdirs();
        }
        return b;
    }

    public static void createFileByMultipartFile(MultipartFile multipartFile, String dir) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = multipartFile.getInputStream();
            String filename = multipartFile.getOriginalFilename();
            out = new FileOutputStream(dir + "/" + filename);
            byte[] bytes = new byte[1024];
            int len;
            while( (len = in.read(bytes)) != -1){
                out.write(bytes,0,len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void delete(String Path){
        File file = new File(Path);
        if(!file.exists())
            return;
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                delete(f.getAbsolutePath()); // 递归删除每一个文件
            }
            file.delete(); // 删除文件夹
        }
    }

    public static void getZip(String srcDir, OutputStream out){
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(out);
            File file = new File(srcDir);
            compress(file,zipOut,file.getName());
        } catch (Exception e){
            logger.error("压缩文件错误");
        } finally{
            if(zipOut != null){
                try {
                    zipOut.close();
                } catch (IOException e) {
                    logger.error("压缩文件错误");
                }
            }
        }
    }

    private static void compress(File file, ZipOutputStream zipOut, String name) throws Exception {
        byte[] buffer = new byte[1024];
        if(file.isFile()){
            zipOut.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(file);
            while((len = in.read(buffer)) != -1){
                zipOut.write(buffer,0,len);
            }
            zipOut.closeEntry();
            in.close();
        }else{
            File[] fileList = file.listFiles();
            if(fileList == null || fileList.length == 0){
                zipOut.putNextEntry(new ZipEntry(name + "/"));
                zipOut.closeEntry();
            }else {
                for(File f : fileList){
                    compress(f,zipOut,name+"/"+f.getName());
                }
            }
        }
    }


}