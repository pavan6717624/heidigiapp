package com.heidigi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.heidigi.repository.HeidigiUserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	HeidigiUserRepository userDetailsRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// System.out.println("entered in loadUserByUsername..." + username);

		Optional<com.heidigi.domain.HeidigiUser> user = userDetailsRepository.findByMobile(Long.valueOf(username));

		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();

		if (user.isPresent()) {
			System.out.println("User is " + user.get().toString() + " " + user.get().getRole().getRoleName());
			roles.add(new SimpleGrantedAuthority(user.get().getRole().getRoleName()));
			return new User(user.get().getMobile() + "", user.get().getPassword(), roles);
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

	}
}
