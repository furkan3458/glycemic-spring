package com.glycemic.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glycemic.model.Country;
import com.glycemic.model.Users;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String fullname;
	
	private String name;
	
	private String surname;

	private String email;
	
	private Country country;
	
	private Boolean enable;
	
	private String createdBy;
	
	private Long createdDate;
	
	private String modifiedBy;
	
	private Long modifiedDate;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String email, String password, String name, String surname, String fullname, 
			Boolean enable, Country country, String createdBy, Long createdDate, String modifiedBy, Long modifiedDate,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.fullname = fullname;
		this.name = name;
		this.surname = surname;
		this.password = password;
		this.country = country;
		this.enable = enable;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(Users user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				user.getId(), 
				user.getEmail(),
				user.getPassword(),
				user.getName(),
				user.getSurname(),
				user.getFullname(),
				user.getEnable(),
				user.getCountry(),
				user.getCreatedBy(),
				user.getCreatedDate(),
				user.getModifiedBy(),
				user.getModifiedDate(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}
	
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getFullname() {
		return fullname;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public Boolean getEnable() {
		return enable;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public Long getCreatedDate() {
		return createdDate;
	}
	
	public String getModifiedBy() {
		return modifiedBy;
	}
	
	public Long getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}