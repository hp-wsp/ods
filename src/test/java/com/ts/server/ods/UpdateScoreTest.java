package com.ts.server.ods;

import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.etask.service.TaskItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class UpdateScoreTest {

    @Autowired
    private TaskItemService service;

    @Test
    public void testScore(){
//        List<TaskItem> items = service.query("", "", "", "", 0, 20000);
//        for(TaskItem item: items){
//            service.update(item);
//        }
    }
}
