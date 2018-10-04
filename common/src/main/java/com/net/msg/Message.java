package com.net.msg;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1123834342L;
    private String uid; // uid
    private int id; // 协议号
    private byte[] data; // 协议内容
    private String from;
}
