package edu.msu.mi.loom

class User {

    transient springSecurityService

    String username
    String password
    String turkerId
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Date dateCreated
    static hasMany = [credentials: CrowdServiceCredentials]

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true, maxSize: 20
        password blank: false
        turkerId nullable: true
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    def isMturkWorker() {
        turkerId==null
    }
}
