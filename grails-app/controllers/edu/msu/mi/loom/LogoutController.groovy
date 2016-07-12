/* Copyright 2013-2015 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.msu.mi.loom

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.security.access.annotation.Secured
import org.springframework.security.web.RedirectStrategy

@Slf4j
@Secured('permitAll')
class LogoutController {
    def springSecurityService
    RedirectStrategy redirectStrategy

    /**
     * Index action. Redirects to the Spring security logout uri.
     */
    def index() {

          log.debug("Leaving")

//        if (!request.post && SpringSecurityUtils.getSecurityConfig().logout.postOnly) {
//            response.sendError HttpServletResponse.SC_METHOD_NOT_ALLOWED // 405
//            return
//        }
       // session.currentUser = springSecurityService.currentUser



        String url = SpringSecurityUtils.securityConfig.logout.filterProcessesUrl+"?reason=${params.reason?:""}&session=${params.sessionId?:""}"
        redirectStrategy.sendRedirect request, response, url

 // '/logoff'

        //TODO why is this critical???  Without this, the user session does not seem to be fully invalided
        request.logout()
        response.flushBuffer()
    }
}
