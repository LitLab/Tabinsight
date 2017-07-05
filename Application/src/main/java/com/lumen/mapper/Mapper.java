package com.lumen.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elirang on 7/5/17.
 */

public class Mapper {

    public static Map<String, Object> toRegisterParams(String firstName, String lastName, String zip, String phone, String childFirstName, String childLastName, String childBirthDate) {
        Map<String, Object> params = new HashMap<>();

        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("zip", zip);
        params.put("phone", phone);
        params.put("childFirstName", childFirstName);
        params.put("childLastName", childLastName);
        params.put("childBirthDate", childBirthDate);

        return params;
    }
}
