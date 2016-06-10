package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.security.SecureRandom

@Slf4j
@Transactional
class UserService {
    private SecureRandom random = new SecureRandom();

    def createUser(String username, String password, String confirmPass) {
        if (password != confirmPass) {
            return [message: "The password and its confirm are not the same."]
        }

        def user = new User(username: username, password: password)
        if (user.save(flush: true)) {
            log.info("Registered user with id ${user.id}")
            addDefaultRole(user)
            return [user: user]
        } else {
            log.error("User creation attempt failed")
            log.error(user?.errors?.dump())
            return [user: user]
        }
    }

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
            return null;
        }
    }

    def createUserWithRandomUsername() {
        def username = makeRandomUsername()
        def password = makeRandomPassword()
        def user = new User(username: username, password: password)

        if (user.save(flush: true)) {
            log.debug("Created ${user.username} with id ${user.id}")
            addDefaultRole(user)
            return user
        } else {
            log.error("User creation attempt failed")
            log.error(user?.errors?.dump())
            return [errors: user?.errors];
        }
    }



    private String makeRandomPassword() {
        return "${System.currentTimeMillis()}"
    }

    private String makeRandomUsername() {
        return new BigInteger(80, random).toString(15);
    }

    private void addDefaultRole(User user) {
        def role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role, true)
    }
}
