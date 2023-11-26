package com.whattoeattoday.recommendationservice.user.request;

import lombok.Data;

/**
 * @author Jiarong Shi js6132@columbia.edu
 * @Date 11/24/2023
 */
@Data
public class UserLoginRequest {
    String username;
    String password;
}
