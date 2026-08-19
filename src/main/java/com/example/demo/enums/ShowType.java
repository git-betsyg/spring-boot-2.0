package com.example.demo.enums;

import lombok.Getter;

/**
 * 错误显示类型
 */
@Getter
public enum ShowType {

    /**
     * 0 静默
     */
    SILENT(0),

    /**
     * 1 警告消息
     */
    WARN(1),

    /**
     * 2 错误消息
     */
    ERROR(2),

    /**
     * 4 通知
     */
    NOTIFICATION(4),

    /**
     * 9 页面
     */
    PAGE(9);

    private final int value;

    ShowType(int value) {
        this.value = value;
    }
}
