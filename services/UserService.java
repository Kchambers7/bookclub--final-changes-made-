ackage com.codingdojo.mvc.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.codingdojo.mvc.models.LoginUser;
import com.codingdojo.mvc.models.User;
import com.codingdojo.mvc.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	// //// REGISTER NEW USER /////////////////////////////

	public User register(User newUser, BindingResult result) {

		if (userRepository.findByEmail(newUser.getEmail()).isPresent()) { // Check if E-mail is already in database
			result.rejectValue("email", "Unique", "This email is already in use!");
		}
		if (userRepository.findByUserName(newUser.getUserName()).isPresent()) {
			result.rejectValue("userName", "Unique", "This Name is already in use!");
		}
		if (!newUser.getPassword().equals(newUser.getConfirm())) { // Check to make sure password matches confirm
																	// password
			result.rejectValue("confirm", "Matches", "The Confirm Password must match Password!");
		}
		if (result.hasErrors()) { // Check if there are errors on the form
			return null;
		} else { // BCrypt hash the password then create a new User
			String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
			newUser.setPassword(hashed);
			System.out.println("---------------------- CREATED NEW USER DB ----------------------");
			return userRepository.save(newUser);
		}
	}

	// //// RETRIEVE USER //////////////////////////////////////////

	public User login(LoginUser newLogin, BindingResult result) {
		if (result.hasErrors()) { // Check form for errors
			return null;
		}

		// Find the User in the database by their email
		Optional<User> potentialUser = userRepository.findByEmail(newLogin.getEmail());
		if (!potentialUser.isPresent()) { // If User not found, do NOT log in User
			result.rejectValue("email", "Unique", "Unknown email!");
			return null;
		}
		User user = potentialUser.get();
		// Check the password given in the form vs the password hash in the database
		if (!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
			result.rejectValue("password", "Matches", "Invalid Password!");
		}
		if (result.hasErrors()) {
			return null;
		} else {
			return user;
		}
	}

	// //// RETRIEVE USER BY ID //////////////////////////

	public User retrieveUser(Long id) {
		Optional<User> optUser = userRepository.findById(id);
		if (optUser.isPresent()) {
			return optUser.get();
		} else {
			return null;
		}
	}
}