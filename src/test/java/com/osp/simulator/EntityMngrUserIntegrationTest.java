package com.osp.simulator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.osp.simulator.model.User;
import com.osp.simulator.repository.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EntityMngrUserIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	UserRepository userRespository;
	
	@Test
	public void whenFindByUserName_thenReturnUser() {
		
		User mytestuser = new User();
		mytestuser.setUsername("mytestuser");
		mytestuser.setPassword("password");
		
		entityManager.persist(mytestuser);
	    entityManager.flush();
	    
	    User found = userRespository.findByUsername("mytestuser");
	    
	    assertThat(found.getUsername())
	    	.isEqualTo(mytestuser.getUsername());
	    
		
	}
}
