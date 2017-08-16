package com.lumen.mapper;

import com.lumen.util.TextUtils2;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for network calls
 */

public class Mapper {

    public static Map<String, Object> toRegisterParams(String firstName, String lastName,
                                                       String zip, String phone, String childFirstName,
                                                       String childLastName, String childBirthDate, String parentEmail,
                                                       String schoolName, String schoolDistrict, String teacherName, String childBirthTime,
                                                       long registerTime) {
        Map<String, Object> params = new HashMap<>();

        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("zip", zip);
        params.put("phone", phone);
        params.put("childFirstName", childFirstName);
        params.put("childLastName", childLastName);
        params.put("childBirthDate", childBirthDate);
        params.put("childBirthTime", childBirthTime);
        params.put("schoolName", schoolName);
        params.put("schoolDistrict", schoolDistrict);
        params.put("teacherName", teacherName);
        params.put("registerTime", registerTime);

        if (TextUtils2.isNotEmpty(parentEmail)) {
            params.put("parentEmail", parentEmail);
        }

        return params;
    }

    public static Map<String, Object> toLoginParams(String userId) {
        Map<String, Object> params = new HashMap<>();

        params.put("userId", userId);

        return params;
    }
}
