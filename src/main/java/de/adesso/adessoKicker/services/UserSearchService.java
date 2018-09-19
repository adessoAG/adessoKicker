package de.adesso.adessoKicker.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adesso.adessoKicker.objects.User;
import de.adesso.adessoKicker.repositories.UserRepository;

@Service
public class UserSearchService {
	
		@Autowired
		private UserRepository userRepository;
		
		private List<User> users;
		
		public List<User> getAllUsers()
		{
			users = new ArrayList<>();
			userRepository.findAll().forEach(users::add);
			return users;
		}
		
		public User getOneUser(long id)
		{
			return userRepository.findById(id).get();
		}
		
		public User getUserSelf(User user)
		{
			return userRepository.findById(user.getUserId()).get();
		}
		
		public void addUser(User user)
		{
			userRepository.save(user);
		}
		
		public void updateUser(User user, long id)
		{
			userRepository.save(userRepository.findById(id).get());
		}
		
		public void deleteUser(long id)
		{
			userRepository.delete(userRepository.findById(id).get());
		}
		
	}
