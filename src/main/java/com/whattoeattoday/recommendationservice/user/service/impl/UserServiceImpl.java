package com.whattoeattoday.recommendationservice.user.service.impl;

import com.whattoeattoday.recommendationservice.common.BaseResponse;
import com.whattoeattoday.recommendationservice.common.PageInfo;
import com.whattoeattoday.recommendationservice.common.Status;
import com.whattoeattoday.recommendationservice.database.request.row.QueryRowRequest;
import com.whattoeattoday.recommendationservice.database.service.TableService;
import com.whattoeattoday.recommendationservice.intratable.request.InsertRequest;
import com.whattoeattoday.recommendationservice.intratable.service.api.IntraTableService;
import com.whattoeattoday.recommendationservice.user.model.User;
import com.whattoeattoday.recommendationservice.user.request.UserLoginRequest;
import com.whattoeattoday.recommendationservice.user.request.UserRegisterRequest;
import com.whattoeattoday.recommendationservice.user.service.api.UserService;
import com.whattoeattoday.recommendationservice.user.utils.MD5Util;
import com.whattoeattoday.recommendationservice.user.utils.UserAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jiarong Shi js6132@columbia.edu
 * @Date 11/24/2023
 */

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private TableService tableService;
    @Resource
    private IntraTableService intraTableService;

    private final static String salt = "1a2b3c";

    @Override
    public BaseResponse userRegister(UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        // valid check
        if (username == null || username.length() == 0) {
            return BaseResponse.with(Status.NOT_FOUND, "Username Not Found");
        }
        if (password == null || password.length() == 0) {
            return BaseResponse.with(Status.NOT_FOUND, "Password Not Found");
        }
        if (email == null || email.length() == 0) {
            return BaseResponse.with(Status.NOT_FOUND, "Email Not Found");
        } else if (!checkEmail(email)) {
            return BaseResponse.with(Status.PARAM_ERROR, "Email Not Valid");
        }
        String encodedPassword = MD5Util.formPassToDBPass(password, salt);
        // check if user has already existed
        User user = getUserByUsername(username);
        if (user != null) {
            return BaseResponse.with(Status.DUPLICATE_ERROR, "User already exists!");
        }
        // insert into db table
        InsertRequest insertRequest = new InsertRequest();
        insertRequest.setTableName("user");
        Map<String, Object> fieldNameValues = new HashMap<>();
        fieldNameValues.put("username", username);
        fieldNameValues.put("password", encodedPassword);
        fieldNameValues.put("email", email);
        fieldNameValues.put("collection", "");
        insertRequest.setFieldNameValues(fieldNameValues);
        BaseResponse response = intraTableService.insert(insertRequest);

        return response;
    }

    @Override
    public BaseResponse userLogin(UserLoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        if (username == null || username.length() == 0) {
            return BaseResponse.with(Status.NOT_FOUND, "Username Not Found");
        }
        if (password == null || password.length() == 0) {
            return BaseResponse.with(Status.NOT_FOUND, "Password Not Found");
        }
        User user = getUserByUsername(username);
        if (user == null) {
            return BaseResponse.with(Status.NOT_FOUND, "User Not Found!");
        }
        String encodedPassword = MD5Util.formPassToDBPass(password, salt);
        if (!encodedPassword.equals(user.getEncodedPassword())) {
            return BaseResponse.with(Status.PARAM_ERROR, "Password Not Valid");
        }

        return BaseResponse.with(Status.SUCCESS, "Login Success!");
    }

    private boolean checkEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private User getUserByUsername(String username) {
        QueryRowRequest queryRowRequest = QueryRowRequest.builder().build();
        queryRowRequest.setTableName("user");
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("username");
        fieldNames.add("password");
        fieldNames.add("email");
        fieldNames.add("collection");
        queryRowRequest.setFieldNames(fieldNames);
        queryRowRequest.setConditionField("username");
        queryRowRequest.setConditionValue(username);
        queryRowRequest.setPageInfo(PageInfo.builder().pageNo(1).pageSize(1).build());
        PageInfo response = tableService.query(queryRowRequest);
        List<Map<String,Object>> data = response.getPageData();
        if (data.size() == 0) {
            return null;
        } else {
            return UserAdapter.adaptQueryData(data.get(0));
        }
    }
}
