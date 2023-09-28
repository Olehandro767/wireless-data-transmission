package ua.edu.ontu.ocs.rest.api.v1.endpoint.user

// TODO remove annotations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v1/admin")
class AdminRestController {

    @PostMapping("/sing-in")
    fun signIn() {
        // response
    }

    @GetMapping("/check-session")
    fun checkSession() {}

    @GetMapping("/sign-out")
    fun signOut() {}

    @PostMapping("/create")
    fun create() {
        // only localhost
    }
}