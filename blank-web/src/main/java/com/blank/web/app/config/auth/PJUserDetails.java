package com.blank.web.app.config.auth;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ymsl.solid.context.auth.BaseUserDetails;
import com.ymsl.solid.web.logging.mdc.MdcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

/**
 * User details for project.
 * Extra information can be stored in PJUserDetails by adding new fields.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PJUserDetails.class, name = "com.blank.web.app.config.auth.PJUserDetails")
})
@Getter
@Setter
public class PJUserDetails extends User implements BaseUserDetails, MdcUser {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userCode;
    private String companyCode;
    private Locale appLocale;

    private Map<String, Serializable> additionalInfo = new HashMap<>();

    public PJUserDetails(String username, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

    }

    public String getMdcUserInfo() {
        return String.format("%s", userCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PJUserDetails userDetails = (PJUserDetails) o;
        if (!userCode.equals(userDetails.getUserCode()) || !appLocale.equals(userDetails.getAppLocale())) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
