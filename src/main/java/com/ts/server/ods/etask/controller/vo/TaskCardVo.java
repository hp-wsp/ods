package com.ts.server.ods.etask.controller.vo;

import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 任务详细信息输出对象
 *
 * @author <a href=mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCardVo {

    /**
     * 任务卡信息
     */
    private final TaskCard card;

    /**
     * 任务卡指标信息集合
     */
    private final List<CardItemVo> items;

    public TaskCardVo(TaskCard card, List<CardItemVo> items) {
        this.card = card;
        this.items = items;
    }

    public TaskCard getCard() {
        return card;
    }

    public List<CardItemVo> getItems() {
        return items;
    }

    /**
     * 指标项输出对象
     */
    public static class CardItemVo {
        /**
         * 指标项目
         */
        private final TaskItem item;

        /**
         * 申报资源集合
         */
        private final List<Declaration> declarations;

        public CardItemVo(TaskItem item, List<Declaration> declarations) {
            this.item = item;
            if(StringUtils.isBlank(item.getGradeLevel())){
                item.setGradeScore(-1);
            }
            this.declarations = declarations;
        }

        public TaskItem getItem() {
            return item;
        }

        public List<Declaration> getDeclarations() {
            return declarations;
        }
    }
}
