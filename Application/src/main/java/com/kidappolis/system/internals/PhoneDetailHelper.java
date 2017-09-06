package com.kidappolis.system.internals;

import com.kidappolis.database.LocalRepo;


public class PhoneDetailHelper {

    public static String getUniqueId() {
        return LocalRepo.getId();
    }
}
