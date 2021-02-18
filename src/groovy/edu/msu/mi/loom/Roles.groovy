package edu.msu.mi.loom

/**
 * Created by Emil Matevosyan
 * Date: 10/7/15.
 */
enum Roles {
    ROLE_ADMIN('ROLE_ADMIN'),
    ROLE_USER('ROLE_USER'),
    ROLE_CREATOR('ROLE_CREATOR');

    String name

    private Roles(String name) {
        this.name = name
    }
}