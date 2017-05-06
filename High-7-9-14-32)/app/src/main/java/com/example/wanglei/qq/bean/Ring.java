package com.example.wanglei.qq.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by wanglei on 2015/6/29.
 */
public class Ring implements Serializable {
    private  static  final long serialVersionUID=1L;
    private String ring_id;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    private  byte[] ring;




    public byte[] getRing() {
        return ring;
    }

    public void setRing(byte[] ring) {

        this.ring = ring;
    }


    public String getRing_id() {
        return ring_id;
    }

    public void setRing_id(String ring_id) {

        this.ring_id = ring_id;
    }

    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }

        FileInputStream fi = new FileInputStream(file);

        byte[] buffer = new byte[(int) fileSize];

        int offset = 0;

        int numRead = 0;

        while (offset < buffer.length

                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

            offset += numRead;

        }

        // 确保所有数据均被读取

        if (offset != buffer.length) {

            throw new IOException("Could not completely read file "
                    + file.getName());

        }

        fi.close();

        return buffer;
    }


}
