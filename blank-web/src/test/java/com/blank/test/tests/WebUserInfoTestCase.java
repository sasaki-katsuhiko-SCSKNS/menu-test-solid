package com.blank.test.tests;

import com.blank.BlankApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Use the {@link BlankApplication} to start the tests.
 * Sometimes it would be difficult to specify only part of the functions to be loaded and tested.
 * All the functions would be loaded, so it would be easy to test, but cost more time.
 */
@SpringBootTest(classes = { BlankApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"development-test", "single-login", "h2-test"})
@Rollback
@AutoConfigureMockMvc
class WebUserInfoTestCase {

    @Autowired
    private MockMvc target;

    @Autowired
    UserDetailsService userDetailsService;

    @Test
    void test() throws Exception {

        // Prepare
        UserDetails ud = userDetailsService.loadUserByUsername("SYS-USER");

        // Execute
        MvcResult result = target.perform(MockMvcRequestBuilders.post("/user/info").with(user(ud))).andExpect(status().isOk()).andReturn();

        // Assert
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("SYS-USER"));
    }
}
