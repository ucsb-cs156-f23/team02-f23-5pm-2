package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

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

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBDiningCommonsMenuItemsRepository UCSBDiningCommonsMenuItemsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/UCSBDiningCommonsMenuItems/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_UCSBDiningCommonsMenuItemss() throws Exception {

                // arrange

                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItems1 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("Portola")
                                .name("Creamy Pesto Pasta")
                                .station("The Brick")
                                .build();


                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItems2 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("Ortega")
                                .name("Veggie Burger")
                                .station("Entree")
                                .build();

                ArrayList<UCSBDiningCommonsMenuItems> expectedItems = new ArrayList<>();
                expectedItems.addAll(Arrays.asList(UCSBDiningCommonsMenuItems1, UCSBDiningCommonsMenuItems2));

                when(UCSBDiningCommonsMenuItemsRepository.findAll()).thenReturn(expectedItems);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedItems);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        // Tests for POST /api/ucsbdiningcommonsmenuitems/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_UCSBDiningCommonsMenuItems() throws Exception {
                // arrange

                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItems1 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("De La Guerra")
                                .name("Pasta Primavera")
                                .station("To Order")
                                .build();

                when(UCSBDiningCommonsMenuItemsRepository.save(eq(UCSBDiningCommonsMenuItems1))).thenReturn(UCSBDiningCommonsMenuItems1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbdiningcommonsmenuitems/post?diningCommonsCode=De La Guerra&name=Pasta Primavera&station=To Order")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).save(UCSBDiningCommonsMenuItems1);
                String expectedJson = mapper.writeValueAsString(UCSBDiningCommonsMenuItems1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
}
