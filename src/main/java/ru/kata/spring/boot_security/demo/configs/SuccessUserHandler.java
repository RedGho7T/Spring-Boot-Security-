package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SuccessUserHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse resp,
                                        Authentication auth) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        logger.debug("Пользователь вошёл с ролями: {}", roles);

        try {
            if (roles.contains("ROLE_ADMIN")) {
                logger.info("Перенаправляем ADMIN на /admin");
                resp.sendRedirect("/admin");
            } else if (roles.contains("ROLE_USER")) {
                logger.info("Перенаправляем USER на /user");
                resp.sendRedirect("/user");
            } else {
                logger.warn("У пользователя нет ролей, направляем на главную");
                resp.sendRedirect("/");
            }
        } catch (Exception e) {
            logger.error("Ошибка в SuccessUserHandler при перенаправлении", e);
            resp.sendRedirect("/");
        }
    }
}