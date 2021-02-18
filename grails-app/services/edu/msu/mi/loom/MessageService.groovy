package edu.msu.mi.loom

import grails.transaction.Transactional
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

@Transactional(readOnly = true)
class MessageService {
    def messageSource

    public Locale getCurrentLocale() {
        try {
            return RequestContextUtils.getLocale(RequestContextHolder.currentRequestAttributes().request)
        } catch (Exception e) {
            return new Locale("en");
        }
    }

    String getMessage(String key) {
        return messageSource.getMessage(key, [].toArray(new String[0]), null, currentLocale)
    }
}
