package com.capstone.coursera.gidma.model.mediator.webdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CheckInQuestion {

    private String qstnId;

    private String qstnDesc;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getQstnId() {
        return qstnId;
    }

    public void setQstnId(String qstnId) {
        this.qstnId = qstnId;
    }

    public String getQstnDesc() {
        return qstnDesc;
    }

    public void setQstnDesc(String qstnDesc) {
        this.qstnDesc = qstnDesc;
    }
}
