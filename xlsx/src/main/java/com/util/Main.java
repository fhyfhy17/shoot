package com.util;

public class Main {
    public static void main(String[] args) {
        String property = System.getProperty("user.dir");
        XlsxToXmlUtil.generate(property);
    }
//    public static void main(String[] args) {
//
//        if (args.length < 1) {
//            System.out.println("缺少第一个参数");
//        }
//
//
//        switch (args[0]) {
//            case "xlsx":
//                XlsxToXmlUtil.generate(System.getProperty("user.dir"), null);
////                XlsxToXmlUtil.generate(args[1], "type");
//                break;
//            case "xls":
//                XlsToxmlUtil.generate(args[1]);
//                break;
//            default:
//                System.out.println("没有匹配的功能，请检查输入");
//                break;
//        }
//    }
}
