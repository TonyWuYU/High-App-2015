package com.example.wanglei.qq.bean;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Created by TonyWu1 on 2015/7/1.
 */
public class Messageinfor implements Serializable{


        private  static  final long serialVersionUID=1L;
        private String fromuid;
        private String touid;
        private String content;

        public String getFromuid() {
            return fromuid;
        }

        public void setFromuid(String fromuid) {
        this.fromuid = fromuid;
    }

        public String getTouid() {
            return touid;
        }

        public void setTouid(String touid) {
            this.touid = touid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }


