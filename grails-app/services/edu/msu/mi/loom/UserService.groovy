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
            return null;
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

    private void addDefaultRole(User user) {
        def role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role, true)
    }
}
