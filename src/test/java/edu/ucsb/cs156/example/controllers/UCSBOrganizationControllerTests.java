package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
    @MockBean
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsborganizations/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganization/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganization/all"))
                            .andExpect(status().is(200)); // logged
    }

    
    @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {


                // arrange


                when(ucsbOrganizationRepository.findById(eq("test"))).thenReturn(Optional.empty());


                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=test"))
                                .andExpect(status().isNotFound()).andReturn();


                // assert


                verify(ucsbOrganizationRepository, times(1)).findById(eq("test"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id test not found", json.get("message"));
        }
 

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsborganization() throws Exception {

            // arrange

            UCSBOrganization carrillo = UCSBOrganization.builder()
                            .orgCode("Carrillo")
                            .orgTranslationShort("carrillo")
                            .orgTranslation("carrillo-dining")
                            .inactive(false)
                            .build();

            UCSBOrganization dlg = UCSBOrganization.builder()
                            .orgCode("dlg")
                            .orgTranslationShort("de-la")
                            .orgTranslation("de-la-guerra")
                            .inactive(false)
                            .build();

            ArrayList<UCSBOrganization> expectedCommons = new ArrayList<>();
            expectedCommons.addAll(Arrays.asList(carrillo, dlg));

            when(ucsbOrganizationRepository.findAll()).thenReturn(expectedCommons);

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsborganization/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbOrganizationRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedCommons);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/ucsborganizations...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganization/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganization/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_org() throws Exception {
            // arrange

            UCSBOrganization ortega = UCSBOrganization.builder()
                            .orgCode("or")
                            .orgTranslationShort("ortega")
                            .orgTranslation("ortega-dining")
                            .inactive(true)
                            .build();

            when(ucsbOrganizationRepository.save(eq(ortega))).thenReturn(ortega);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsborganization/post?orgCode=or&orgTranslationShort=ortega&orgTranslation=ortega-dining&inactive=true")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbOrganizationRepository, times(1)).save(ortega);
            String expectedJson = mapper.writeValueAsString(ortega);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

      // Tests for GET /api/ucsborganization?...


      @Test
      public void logged_out_users_cannot_get_by_id() throws Exception {
              mockMvc.perform(get("/api/ucsborganization?orgCode=test"))
                              .andExpect(status().is(403)); // logged out users can't get by id
      }
     
      @WithMockUser(roles = { "USER" })
      @Test
      public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {


              // arrange


              UCSBOrganization orgs = UCSBOrganization.builder()
                      .orgCode("or")
                      .orgTranslationShort("ortega")
                      .orgTranslation("ortega-dining")
                      .inactive(false)
                      .build();


              when(ucsbOrganizationRepository.findById(eq("or"))).thenReturn(Optional.of(orgs));


              // act
              MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=or"))
                              .andExpect(status().isOk()).andReturn();


              // assert


              verify(ucsbOrganizationRepository, times(1)).findById(eq("or"));
              String expectedJson = mapper.writeValueAsString(orgs);
              String responseString = response.getResponse().getContentAsString();
              assertEquals(expectedJson, responseString);
      }

      // Tests for DELETE /api/ucsbdiningcommons?...


      @WithMockUser(roles = { "ADMIN", "USER" })
      @Test
      public void admin_can_delete_a_date() throws Exception {
              // arrange


              UCSBOrganization test = UCSBOrganization.builder()
                              .orgCode("test")
                              .orgTranslationShort("testing")
                              .orgTranslation("testings")
                              .inactive(false)
                              .build();


              when(ucsbOrganizationRepository.findById(eq("test"))).thenReturn(Optional.of(test));


              // act
              MvcResult response = mockMvc.perform(
                              delete("/api/ucsborganization?orgCode=test")
                                              .with(csrf()))
                              .andExpect(status().isOk()).andReturn();


              // assert
              verify(ucsbOrganizationRepository, times(1)).findById("test");
              verify(ucsbOrganizationRepository, times(1)).delete(any());


              Map<String, Object> json = responseToJson(response);
              assertEquals("UCSOrganization with id test deleted", json.get("message"));
      }


      @WithMockUser(roles = { "ADMIN", "USER" })
      @Test
      public void admin_tries_to_delete_non_existant_orgs_and_gets_right_error_message()
                      throws Exception {
              // arrange


              when(ucsbOrganizationRepository.findById(eq("test"))).thenReturn(Optional.empty());


              // act
              MvcResult response = mockMvc.perform(
                              delete("/api/ucsborganization?orgCode=test")
                                              .with(csrf()))
                              .andExpect(status().isNotFound()).andReturn();


              // assert
              verify(ucsbOrganizationRepository, times(1)).findById("test");
              Map<String, Object> json = responseToJson(response);
              assertEquals("UCSBOrganization with id test not found", json.get("message"));
      }

      // Tests for PUT /api/ucsbdiningcommons?...


      @WithMockUser(roles = { "ADMIN", "USER" })
      @Test
      public void admin_can_edit_an_existing_organization() throws Exception {
              // arrange


              UCSBOrganization test = UCSBOrganization.builder()
                              .orgCode("t")
                              .orgTranslationShort("test")
                              .orgTranslation("testing")
                              .inactive(false)
                              .build();


              UCSBOrganization test_edit = UCSBOrganization.builder()
                              .orgCode("te")
                              .orgTranslationShort("tests")
                              .orgTranslation("testings")
                              .inactive(true)
                              .build();


              String requestBody = mapper.writeValueAsString(test_edit);


              when(ucsbOrganizationRepository.findById(eq("t"))).thenReturn(Optional.of(test));


              // act
              MvcResult response = mockMvc.perform(
                              put("/api/ucsborganization?orgCode=t")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .characterEncoding("utf-8")
                                              .content(requestBody)
                                              .with(csrf()))
                              .andExpect(status().isOk()).andReturn();


              // assert
              verify(ucsbOrganizationRepository, times(1)).findById("t");
              verify(ucsbOrganizationRepository, times(1)).save(test_edit); // should be saved with updated info
              String responseString = response.getResponse().getContentAsString();
              assertEquals(requestBody, responseString);
      }



      @WithMockUser(roles = { "ADMIN", "USER" })
      @Test
      public void admin_cannot_edit_orgs_that_does_not_exist() throws Exception {
              // arrange


              UCSBOrganization editedOrganization = UCSBOrganization.builder()
                              .orgCode("t")
                              .orgTranslationShort("test")
                              .orgTranslation("testing")
                              .inactive(false)
                              .build();


              String requestBody = mapper.writeValueAsString(editedOrganization);


              when(ucsbOrganizationRepository.findById(eq("t"))).thenReturn(Optional.empty());


              // act
              MvcResult response = mockMvc.perform(
                              put("/api/ucsborganization?orgCode=t")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .characterEncoding("utf-8")
                                              .content(requestBody)
                                              .with(csrf()))
                              .andExpect(status().isNotFound()).andReturn();


              // assert
              verify(ucsbOrganizationRepository, times(1)).findById("t");
              Map<String, Object> json = responseToJson(response);
              assertEquals("UCSBOrganization with id t not found", json.get("message"));


      }
}
