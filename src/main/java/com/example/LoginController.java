package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by doug on 9/20/16.
 */
@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;

    // requires user to be logged in
    @RequestMapping(value = "/")
    public String home(HttpSession session, Model model){
        if(session.getAttribute("userId") == null){
            return "redirect:/login";
        } else {
            // get the user
            Integer userId = (Integer) session.getAttribute("userId");
            User user = userRepository.getOne(userId);
            model.addAttribute("user", user);

            return "home";
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(){
        return "loginForm";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String email, String password, HttpSession session, Model model) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {

        User user = userRepository.getByEmail(email);

        if(user != null && PasswordStorage.verifyPassword(password, user.getPassword())){
            session.setAttribute("userId", user.getId());
            return "redirect:/";
        } else {
            model.addAttribute("loginFailed", true);
            return "loginForm";
        }

    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(){
        return "registerForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(User user) throws PasswordStorage.CannotPerformOperationException {
        user.setPassword(PasswordStorage.createHash(user.getPassword()));

        userRepository.save(user);

        return "redirect:/";
    }

    @RequestMapping(path = "/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

}
