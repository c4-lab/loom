package edu.msu.mi.loom


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{


    def redirectStrategy
    def grailsApplication

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getDetails() != null) {
            try {
                httpServletRequest.getSession().invalidate();
                System.out.println("User Successfully Logout");
                //you can add more codes here when the user successfully logs out,
                //such as updating the database for last active.
            } catch (Exception e) {
                e.printStackTrace();
                e = null;
            }
        }
        println(httpServletRequest.parameterMap)
        String reason = httpServletRequest.getParameter("reason")
        String target = "not-found"
        String code = null
        switch (reason) {
            case "full":
                target = "is-full"
                Session.withNewSession {
                    code = Session.get(httpServletRequest.getParameter("session")).fullCode
                }
                break

            case "done":
                target = "is-done"
                Session.withNewSession {
                    code = Session.get(httpServletRequest.getParameter("session")).doneCode
                }
                break

            default:
                target = "not-found"

        }


        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.sendRedirect("/loom/${target}${code?"?code=${code}":""}")
    }
}
