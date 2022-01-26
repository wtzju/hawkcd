package com.amazonaws.guru.rules.securityrules.generic.java;

import java.util.Properties;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.jndi.JndiTemplate;
import org.springframework.ldap.core.LdapTemplate;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogInjection {
    
    public void positive1(@RequestParam String inputName) {
        log.info("Input is: {}", inputName);
    }
    
    public void positive2(ServletRequest request) {
        String xValue = request.getParameter("x");
        log.info("Value is: {}", xValue);
    }
    
    public void positive3(ServletRequest request) {
        log.info("Value is: {}", request.getParameter("x"));
    }

    @RequestMapping("/example.htm")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView result = new ModelAndView("success");
        String userId = request.getParameter("userId");
        result.addObject("userId", userId);
        // More logic to populate `result`.
        log.info("Successfully processed {} with user ID: {}.", request, userId);
        return result;
    }
    
    @RequestMapping("/example.htm")
    public ModelAndView handleRequestSafely(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView result = new ModelAndView("success");
        String userId = request.getParameter("userId");
        String sanitizedUserId = sanitize(userId);
        result.addObject("userId", sanitizedUserId);
        // More logic to populate `result`.
        log.info("Successfully processed {} with user ID: {}.", request, sanitizedUserId);
        return result;
    }
    
    private static String sanitize(String userId) {
        return userId.replaceAll("\\D", "");
    }
    
    public void negative1(SomeObject obj) {
        String value = obj.getParameter("x");
        log.info("Value is: {}", value);
    }

    public void negative2(@SomeAnnotation String inputName) {
        log.info("Input is: {}", inputName);
    }
    
    public void negative3(@RequestParam String inputName) {
        log.info("Input is: {}", sanitize(inputName));
    }
}