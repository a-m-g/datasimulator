package com.osp.simulator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.osp.simulator.model.User;
import com.osp.simulator.repository.UserRepository;
import com.osp.simulator.service.UserService;
import com.osp.simulator.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
public class MockedUserServiceImplIntegrationTest {

	@TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }
	
	@Autowired
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	@Before
	public void setUp() {
		User mytestuser = new User();
		mytestuser.setUsername("myserviceuser");
		mytestuser.setPassword("password");
		
		Mockito.when(userService.findByUsername("myserviceuser"))
			.thenReturn(mytestuser);
	}
	
	@Test
	public void findNameofMockedUser() {
		
		User myServiceUser = userService.findByUsername("myserviceuser");
		assertThat(myServiceUser.getUsername().equals("myserviceuser"));
	}
	
	@Test
	public void findPasswordofMockedUser() {
		
		User myServiceUser = userService.findByUsername("myserviceuser");
		assertThat(myServiceUser.getPassword().equals("password"));
	}
	
	
}
