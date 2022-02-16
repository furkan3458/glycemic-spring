package com.glycemic.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.glycemic.validator.UserValidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Users extends BaseModel implements Serializable{

	private static final long serialVersionUID = -1915197722847011881L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "Email kısmı boş bırakılamaz.", groups = UserValidator.class)
	@Size(max = 50, message = "Email en fazla 50 karakter içerebilir.", groups = UserValidator.class)
	@Column(unique=true,length = 50)
	private String email;
	
	@NotNull(message = "şifre kısmı boş bırakılamaz.", groups = UserValidator.class)
	private String password;
	
	@NotNull(message = "İsim kısmı boş bırakılamaz.", groups = UserValidator.class)
	private String name;
	
	@NotNull(message = "Soyisim kısmı boş bırakılamaz.", groups = UserValidator.class)
	private String surname;

	private String fullname;
	
	private Boolean enable;
	
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@NotNull(message = "Şehir kısmı boş bırakılamaz.", groups = UserValidator.class)
	private City city;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Roles> roles;
	
	public Users(Long id, String email, String password, String name, String surname, 
			String fullname, Boolean enable, City city) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.fullname = fullname;
		this.enable = enable;
		this.city = city;
	}
	
	public Users(String email, String password, String name, String surname, 
			Boolean enable) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.enable = enable;
	}
}
