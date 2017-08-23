package com.lumen.system.internals;

import com.lumen.database.LocalRepo;


public class PhoneDetailHelper {

    public static String getUniqueId() {
        return LocalRepo.getId();
    }
}
