package ua.edu.ontu.ocs.rest.api.v1_0_0.controller.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ua.edu.ontu.ocs.rest.api.RestVersions.V1_0_0;;

@RestController
@RequestMapping(V1_0_0 + "/admin")
public class AdminRestController {

	@GetMapping("/login")
	public void login() {
		// TODO
	}

	@GetMapping("/logout")
	public void logout() {
		// TODO
	}

	@GetMapping("/read/all")
	public void readAll() {
		// TODO
	}

	@PostMapping("/create")
	public void create() {
		// TODO
	}

	@PutMapping("/update")
	public void update() {
		// TODO
	}

	@DeleteMapping("/delete")
	public void delete() {
		// TODO
	}
}
