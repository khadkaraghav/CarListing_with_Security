package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute ("user") User user, BindingResult result, Model model) {

        model.addAttribute("user", user);

        if(result.hasErrors()){

            return "registration";
        }

        else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/admin")
    public String admin(){
        return "admin";
    }


    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        User myuser = ((CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal)
                        .getPrincipal())
                .getUser();
        model.addAttribute("myuser", myuser);
        return "secure";
    }


    @GetMapping("/")
    public String index(Model model,@ModelAttribute Category category,@ModelAttribute Car car){
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "index";
    }


    @RequestMapping("/addCategory")
    public String addCategory(Model model){
        model.addAttribute("newCategory", new Category());
        model.addAttribute("count", categoryRepository.count());
        model.addAttribute("listCategory", categoryRepository.findAll());
        return "addcategory";
    }

    @PostMapping("/processCategory")
    public String processCategory(@ModelAttribute Category category, Model model){

        categoryRepository.save(category);
        model.addAttribute("newCategory", category);
        model.addAttribute("listCategory", categoryRepository.findAll());
        model.addAttribute("count", 1);

        return "addcategory";
    }

    @RequestMapping("/addCar")
    public String addCar(Model model){
        if(categoryRepository.count() < 1){

            return "redirect:/addCategory";
        }
        else{
            model.addAttribute("car",new Car());
            model.addAttribute("categories", categoryRepository.findAll());
            return "addcar";
        }

    }

    @PostMapping("/carProcess")
    public String processCar(@ModelAttribute Car cars, @RequestParam("file") MultipartFile file){

        if(file.isEmpty()){
            return "redirect:/addCar";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcestype", "auto"));
            cars.setImage(uploadResult.get("url").toString());


            carRepository.save(cars);


        }catch(IOException e){
            e.printStackTrace();
            return "redirect:/addCar";
        }
        return "redirect:/";
    }

    @RequestMapping("/details/{id}")
    public String details(@PathVariable("id") long id, Model model){


        model.addAttribute("car", carRepository.findById(id).get());
        return "detail";

    }

    @RequestMapping("/categoryDetail/{id}")
    public String catDetails(@PathVariable("id") long id, Model model){

        model.addAttribute("vehicle", carRepository.findAll());
        model.addAttribute("details", categoryRepository.findById(id).get());
        return "categoryDetail";

    }

    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepository.findById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "addcar";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCar(@PathVariable("id") long id){

        carRepository.deleteById(id);
        return "redirect:/";
    }


}