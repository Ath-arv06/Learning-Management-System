package com.college.lms.identity.entity;

/** Account lifecycle statuses. */
public enum UserStatus {
    /** Email verified, account usable. */
    ACTIVE,
    /** Account suspended by administrator. Cannot login. */
    SUSPENDED,
    /** Registered but email not yet verified. */
    PENDING_VERIFICATION
}
