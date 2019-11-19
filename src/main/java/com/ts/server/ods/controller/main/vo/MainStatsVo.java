package com.ts.server.ods.controller.main.vo;

import com.ts.server.ods.evaluation.domain.Evaluation;

import java.util.Map;

/**
 * 首页统计输出对象
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class MainStatsVo {
    private final boolean has;
    private final Evaluation evaluation;
    private final int companyCount;
    private final Map<String, Integer> statusCount;

    public MainStatsVo(Evaluation evaluation, int companyCount, Map<String, Integer> statusCount) {
        this.has = evaluation != null;
        this.evaluation = evaluation;
        this.companyCount = companyCount;
        this.statusCount = statusCount;
    }

    public boolean isHas() {
        return has;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getCompanyCount() {
        return companyCount;
    }

    public Map<String, Integer> getStatusCount() {
        return statusCount;
    }
}
