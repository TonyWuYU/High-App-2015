package com.xueba.quting.high.other;

/**
 * Created by wanglei on 2015/6/13.
 */

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private String SDPATH;

   /* public String getSDPATH() {
        return SDPATH;
    }*/
    public FileUtils() {
        //得到当前外部存储设备的目录
        //  File.separator 文件分隔线
        SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }
    /**
     * 在SD卡上创建文件
     *
     * @throws java.io.IOException
     */
    public File creatFileInSDCard(String fileName, String dir) throws IOException {
        File file = new File(SDPATH + dir + File.separator + fileName);
        System.out.println("file--->"+file);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName+File.separator);
        //创建文件夹
        dir.mkdirs();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName, String path){
        //File file = new File(SDCardRoot + path + File.separator + fileName);
        //File file = new File(path + File.separator + fileName);
        File dir = new File (File.separator + path);
        File file = new File(dir, fileName);
        //System.out.println("file " + fileName + " exists?" + file.exists());
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public File write2SDFromInput(String path,String fileName,InputStream input){
        File dir = null;
        File file = null;
        FileOutputStream output = null;
        try{
            dir = creatSDDir(path);
            //file = creatFileInSDCard(fileName, path);
            //output = new FileOutputStream(file);
            file = new File(dir, fileName);
            //output = fileContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            output = new FileOutputStream(file);
            byte buffer [] = new byte[4 * 1024];
            int temp;
            while((temp = input.read(buffer)) != -1){
                output.write(buffer, 0, temp);
            }
            output.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                output.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }

    //读取目录中MP3文件的名称和大小
   /* public List<Mp3Info> getMp3Files(String path){
        //创建一个 List<Mp3Info>列表
        List<Mp3Info> mp3Infos =new ArrayList<Mp3Info>();
        //由指定路径获取文件夹
        File file = new File(SDPATH +  File.separator +path);
        //获取文件夹下的文件数组
       File[] files= file.listFiles();
        /*取出文件数组的文件并将每个文件名和大小赋值给对应的Mp3Info对象
        * 并添加到 List<Mp3Info>列表中*/
      /*  for(int i=0;i<files.length;i++){

            if(files[i].getName().endsWith("mp3")){
                Mp3Info mp3info=new Mp3Info();
                mp3info.setMap3Name(files[i].getName());
                mp3info.setMp3Size(files[i].length()+"");
                mp3Infos.add(mp3info);
           }

        }
        return mp3Infos;
    }*/

}
