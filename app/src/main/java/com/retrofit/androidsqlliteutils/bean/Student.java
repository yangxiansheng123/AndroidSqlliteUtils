package com.retrofit.androidsqlliteutils.bean;

import com.retrofit.androidsqlliteutils.annotation.DbField;
import com.retrofit.androidsqlliteutils.annotation.DbTable;

/**
 * @author :created by ${yangpf}
 * 时间:2018/6/8 18
 * 邮箱：xxx@.qq.com
 */
@DbTable("tb_student")
public class Student {
    @DbField("_id")
    private Integer id;
    private String age;
    private String sex;
    private String hight;

    public Student(String age, String sex, String hight) {
        this.age = age;
        this.sex = sex;
        this.hight = hight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHight() {
        return hight;
    }

    public void setHight(String hight) {
        this.hight = hight;
    }
}
