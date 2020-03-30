package com.mll.mp.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;

import static org.apache.shiro.web.util.WebUtils.SAVED_REQUEST_KEY;

public class ShiroUtils {
    public static SavedRequest getSavedRequest(){
        SavedRequest savedRequest = null;
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(false);
        if (session!=null){
            savedRequest = (SavedRequest)session.getAttribute(SAVED_REQUEST_KEY);

        }
        return savedRequest;
    }
}
