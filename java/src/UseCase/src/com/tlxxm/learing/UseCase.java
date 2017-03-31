package com.tlxxm.learing;

import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {
    public int id();
    public String description() default "描述默认值";
}
class PassWordUtils{
    @UseCase(id=1,description = "密码校验")
    public boolean validatePassword(String password){
        return (password.matches("\\w*\\d\\w*"));
    }
}
