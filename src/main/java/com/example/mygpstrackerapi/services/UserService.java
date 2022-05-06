package com.example.mygpstrackerapi.services;

import com.example.mygpstrackerapi.models.FirebaseUsers;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	public FirebaseUsers getUser(String id) {
		try {
			Firestore dbFirestore = FirestoreClient.getFirestore();
			DocumentReference docRef = dbFirestore.collection("users").document(id);
			DocumentSnapshot document = docRef.get().get();

			if (document.exists()) {
				return document.toObject(FirebaseUsers.class);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getUserIdByToken(String token) {
		try {
			token = token.replace("Bearer ", "");
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			String uid = decodedToken.getUid();

			return uid;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean isAdmin(String id) {
		FirebaseUsers user = getUser(id);

		if (user != null) {
			return user.isAdmin();
		}
		return false;
	}

	public boolean isVerified(String id) {
		FirebaseUsers user = getUser(id);

		if (user != null) {
			return user.isVerified();
		}
		return false;
	}
}
