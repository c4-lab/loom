package edu.msu.mi.loom

import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache = true, includeNames = true, includePackage = false)
class UserRole implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Roles role

    UserRole(User u, Roles r) {
        this()
        user = u
        role = r
    }

    @Override
    boolean equals(other) {
        if (!(other instanceof UserRole)) {
            return false
        }

        other.user?.id == user?.id && other.role?.id == role?.id
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static UserRole get(long userId, long roleId) {
        criteriaFor(userId, roleId).get()
    }

    static boolean exists(long userId, long roleId) {
        criteriaFor(userId, roleId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long roleId) {
        UserRole.where {
            user == User.load(userId) &&
                    role == Roles.load(roleId)
        }
    }

    static UserRole create(User user, Roles role, boolean flush = false) {
        def instance = new UserRole(user, role)
        instance.save(flush: flush, insert: true)
        instance
    }

    static boolean remove(User u, Roles r, boolean flush = false) {
        if (u == null || r == null) return false

        int rowCount = UserRole.where { user == u && role == r }.deleteAll()

        if (flush) {
            UserRole.withSession { it.flush() }
        }

        rowCount
    }

    static void removeAll(User u, boolean flush = false) {
        if (u == null) return

        UserRole.where { user == u }.deleteAll()

        if (flush) {
            UserRole.withSession { it.flush() }
        }
    }

    static void removeAll(Roles r, boolean flush = false) {
        if (r == null) return

        UserRole.where { role == r }.deleteAll()

        if (flush) {
            UserRole.withSession { it.flush() }
        }
    }

    static constraints = {
        role validator: { Roles r, UserRole ur ->
            if (ur.user == null || ur.user.id == null) return
            boolean existing = false
            UserRole.withNewSession {
                existing = UserRole.exists(ur.user.id, r.id)
            }
            if (existing) {
                return 'userRole.exists'
            }
        }
    }

    static mapping = {
        id composite: ['user', 'role']
        version false
    }
}
