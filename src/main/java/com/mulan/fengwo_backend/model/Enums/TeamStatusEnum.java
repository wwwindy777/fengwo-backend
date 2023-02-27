package com.mulan.fengwo_backend.model.Enums;

public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    LOCKED(2,"加密");

    private final int statusNum;
    private final String describe;

    TeamStatusEnum(int statusNum, String describe) {
        this.statusNum = statusNum;
        this.describe = describe;
    }

    /**
     * 根据状态值判断状态
     * @param statusNum
     * @return
     */
    public static TeamStatusEnum getStatusByNum(Integer statusNum){
        if (statusNum == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum value : values) {
            if (value.getStatusNum() == statusNum){
                return value;
            }
        }

        return null;
    }
    public int getStatusNum() {
        return statusNum;
    }

    public String getDescribe() {
        return describe;
    }
}
