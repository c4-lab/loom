import edu.msu.mi.loom.Role
import edu.msu.mi.loom.Roles
import edu.msu.mi.loom.User
import edu.msu.mi.loom.UserRole

class BootStrap {

    def init = { servletContext ->
        environments {
            development {
                createInitialRecords()
            }
        }
    }
    def destroy = {
    }

    private createInitialRecords() {
        def adminRole = Role.findWhere(authority: Roles.ROLE_ADMIN.name) ?: new Role(authority: Roles.ROLE_ADMIN.name).save(failOnError: true)
        def creatorRole = Role.findWhere(authority: Roles.ROLE_CREATOR.name) ?: new Role(authority: Roles.ROLE_CREATOR.name).save(failOnError: true)

        def admin = User.findWhere(username: 'admin') ?: new User(username: 'admin', password: '1').save(failOnError: true)


        if (!admin.authorities.contains(adminRole)) {
            UserRole.create(admin, adminRole)
        }

        if (!admin.authorities.contains(creatorRole)) {
            UserRole.create(admin, creatorRole)
        }
    }
}
