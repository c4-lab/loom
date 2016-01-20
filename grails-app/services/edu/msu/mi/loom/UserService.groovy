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

    def deleteUser(User user) {
        def role = UserRole.findByUser(user).role
        if (role?.authority == 'ROLE_USER') {
            UserRole.remove(user, role)
            def rounds = Round.findAllByUser(user)
            rounds.each {
                it.delete()
            }

            def tempExperiments = TempExperiment.findAllByUser(user)
            tempExperiments.each {
                it.delete()
            }

            def tempSimulations = TempSimulation.findAllByUser(user)
            tempSimulations.each {
                it.delete()
            }

            user.delete(flush: true)
            log.debug("User has been deleted")
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
