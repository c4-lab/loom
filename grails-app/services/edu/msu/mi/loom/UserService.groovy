package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class UserService {

    def createUser(String username) {
        def password = makeRandomPassword();
        def user = new User(username: username, password: password)

        if (user.save(flush: true)) {
            log.info("Created user with id ${user.id}")
            addDefaultRole(user)
            return user
        } else {
            log.error("User creation attempt failed")
            log.error(user?.errors?.dump())
        }

    }

    private String makeRandomPassword() {
        return "${System.currentTimeMillis()}"
    }

    private void addDefaultRole(User user) {
        def role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role)
    }
}
