package com.boco.activiti.bean;

import java.io.Serializable;

public class ProcessAuditResult implements Serializable {
    /**
     * 处理结果   1.通过  2 驳回
     */
    private int resultCode;

    /**
     * 是否继续处理  1.继续   2.处理完成
     */
    private int contineProcessCode;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getContineProcessCode() {
        return contineProcessCode;
    }

    public void setContineProcessCode(int contineProcessCode) {
        this.contineProcessCode = contineProcessCode;
    }
}
