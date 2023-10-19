package com.whattoeattoday.recommendationservice;

import com.whattoeattoday.recommendationservice.common.BaseResponse;
import com.whattoeattoday.recommendationservice.common.Status;
import com.whattoeattoday.recommendationservice.intratable.request.*;
import com.whattoeattoday.recommendationservice.intratable.service.api.IntraTableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;
/**
 * @author Yufeng Wan yw3921@columbia.edu
 * @date 10/16/23
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RecommendationServiceApplication.class)
public class IntraTableServiceImplTest {
    @Resource
    private IntraTableService intraTableService;

    @Test
    public void testInsert(){
        InsertRequest request = new InsertRequest();
        request.setTableName("test1016");
        Map<String, Object> fieldNameValues = new HashMap<>();
        fieldNameValues.put("name", "Le");
        fieldNameValues.put("gender","Male");
        fieldNameValues.put("age", 2);
        request.setFieldNameValues(fieldNameValues);
        BaseResponse response = intraTableService.insert(request);
        log.info("RESPONSE: {}", response);
        Assert.assertEquals(response.getCode(), Status.SUCCESS);
    }

    @Test
    public void testDelete(){
        DeleteRequest request = new DeleteRequest();
        request.setTableName("test1016");
        request.setConditionField("name");
        request.setConditionValue("Lee");
        BaseResponse response = intraTableService.delete(request);
        log.info("RESPONSE: {}", response);
        Assert.assertEquals(response.getCode(), Status.SUCCESS);
    }

    @Test
    public void testUpdate(){
        UpdateRequest request = new UpdateRequest();
        request.setTableName("test1016");
        request.setConditionField("age");
        request.setConditionValue(4);
        Map<String,Object>map = new HashMap<>();
        map.put("gender","Male");
        map.put("age",6);
        request.setFieldNameValues(map);
        BaseResponse response = intraTableService.update(request);
        log.info("RESPONSE: {}", response);
        Assert.assertEquals(response.getCode(), Status.SUCCESS);
    }
}
