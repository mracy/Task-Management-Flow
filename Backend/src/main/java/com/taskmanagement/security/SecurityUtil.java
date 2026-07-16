package com.taskmanagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    public static String getCurrentUserAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null
                && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }

    public static boolean hasRole(String role) {
        String authority = getCurrentUserAuthority();
        return authority != null && authority.equals("ROLE_" + role);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isManager() {
        return hasRole("MANAGER");
    }

    public static boolean isEmployee() {
        return hasRole("EMPLOYEE");
    }
}
