package edu.msu.mi.loom

import grails.transaction.Transactional
import org.springframework.util.StringUtils

@Transactional(readOnly = true)
class EmailService {
    def mailService
    def messageService
    def groovyPageRenderer
    def grailsApplication
    def grailsLinkGenerator

    def sendInvitationEmail(String emails, def id) {
        List<String> emailList = parseEmailsString(emails)
        String title = messageService.getMessage("email.invitation.title")
        String link = grailsLinkGenerator.link(controller: 'home', action: 'joinByEmail', params: [id: id], absolute: true)
        runAsync {
            User.withSession {
                sendEmailHtml(emailList, title, [link: link])
            }
        }
    }

    def sendEmailHtml(List targets, String title, Map model) {
        if (grailsApplication.config.grails.mail.enabled) {
            try {
                def view = '/email/email'
                def content = groovyPageRenderer.render(view: view, model: [title: title] + model)
                for (String target : targets) {
                    mailService.sendMail {
                        from "${grailsApplication.config.grails.mail.displayName} <${grailsApplication.config.grails.mail.username}>"
                        to target
                        subject title
                        html(content)
                    }
                }
            } catch (Exception e) {
                log.warn("Could not send email: ${e.class} - ${e.message}")
                log.warn("Email content: ${targets} -- ${title} -- $model")
            }
        } else {
            log.info("Sending email")
            log.info("-----------")
            log.info("to: ${targets}")
            log.info("subject: ${title}")
            log.info("-----------")
            log.info("model:")
            log.info(model)
            log.info("-----------")
        }
    }

    private List<String> parseEmailsString(final String emailsString) {
        List<String> emailList = new ArrayList<>()
        for (String email : StringUtils.commaDelimitedListToStringArray(emailsString)) {
            email = email.trim();
            if (email.length() > 0) {
                emailList.add(email);
            }
        }

        return emailList
    }

}
