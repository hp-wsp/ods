package com.ts.server.ods;

import com.ts.server.ods.taskcard.service.TaskItemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class UpdateScoreTest {

    @Autowired
    private TaskItemService service;

    @Test
    public void testScore(){
//        List<TaskCardItem> items = service.query("", "", "", "", 0, 20000);
//        for(TaskCardItem item: items){
//            service.update(item);
//        }
    }
}
