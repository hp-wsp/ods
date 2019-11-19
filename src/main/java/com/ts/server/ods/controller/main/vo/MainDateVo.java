package com.ts.server.ods.controller.main.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 首页测评时间线
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class MainDateVo {
    @ApiModelProperty("日期")
    private final String date;
    @ApiModelProperty("详情集合")
    private final List<DateDetail> details;

    public MainDateVo(String date) {
        this.date = date;
        this.details = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public List<DateDetail> getDetails() {
        return details;
    }

    public void addDetail(DateDetail dateDetail){
        this.details.add(dateDetail);
    }

    public static class DateDetail {
        @ApiModelProperty("详情")
        private final String detail;
        @ApiModelProperty("用户名")
        private final String username;
        @ApiModelProperty("时间")
        private final Date time;

        public DateDetail(String detail, String username, Date time) {
            this.detail = detail;
            this.username = username;
            this.time = time;
        }

        public String getDetail() {
            return detail;
        }

        public String getUsername() {
            return username;
        }

        public Date getTime() {
            return time;
        }
    }
}
