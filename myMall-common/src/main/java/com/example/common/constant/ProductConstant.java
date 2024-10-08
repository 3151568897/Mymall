package com.example.common.constant;

public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");
        private int code;
        private String msg;

        AttrEnum(int code, String msg){ {
         this.code = code;
         this.msg = msg;
        }}
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }

    public enum SpuStatusEnum{
        DOWN(2, "下架"),
        UP(1, "上架"),
        CREATED(0, "新建");
        private int code;
        private String msg;

        SpuStatusEnum(int code, String msg){ {
            this.code = code;
            this.msg = msg;
        }}
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }
}
