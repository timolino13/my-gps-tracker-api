package com.example.mygpstrackerapi.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FirebaseUsers {
	private String id;
	private String email;
	private FirebaseRoles roles;

	public boolean isAdmin() {
		if (roles != null) {
			return roles.isAdmin();
		} else {
			return false;
		}
	}
}
