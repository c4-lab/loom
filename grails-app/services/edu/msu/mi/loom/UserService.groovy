package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.security.SecureRandom

@Slf4j
@Transactional
class UserService {
    private SecureRandom random = new SecureRandom();
    def randomStringGenerator

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

    def createUserByWorkerId(String workerId, Roles role = null) {
        def password = makeRandomPassword();
        def username = workerId
        while (User.countByUsername(username)) {
            username = workerId+"_"+randomStringGenerator.generateLowercase(4)
        }

        def  user = new User(username: username, password: password, workerId: workerId)
        if (!user.save(flush: true)) {
            log.error("User creation attempt failed")
            log.error(user?.errors?.dump())
            return null;
        }
        if (role == Roles.ROLE_MTURKER){

            addMturkerRole(user)
            log.info("Attempting to create Mturk user with id ${user.id}")

        } else if (role == Roles.ROLE_PROLIFIC) {
            addProlificRole(user)
            log.info("Attempting to create Prolific user with id ${user.id}")

        } else {
            log.info("Attempting to create regular user with id ${user.id}")
            addDefaultRole(user)
        }
        return user


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

    private void addMturkerRole(User user) {
        def role = Role.findByAuthority(Roles.ROLE_MTURKER.name)
        UserRole.create(user, role, true)
    }

    private void addProlificRole(User user) {
        def role = Role.findByAuthority(Roles.ROLE_PROLIFIC.name)
        UserRole.create(user, role, true)
    }
}
