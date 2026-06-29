package ni.edu.uam.psyconnect_backend.controller;

import ni.edu.uam.psyconnect_backend.model.Psychologist;
import ni.edu.uam.psyconnect_backend.service.PsychologistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PsychologistService service;

    public AdminController(PsychologistService service) {
        this.service = service;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("psychologists", service.getAll());
        model.addAttribute("psychologist", new Psychologist());
        return "admin";
    }

    @PostMapping("/save")
    public String savePsychologist(
            @ModelAttribute Psychologist psychologist,
            @RequestParam("photoFile") MultipartFile photoFile
    ) {
        try {
            if (photoFile != null && !photoFile.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(photoFile.getBytes());
                psychologist.setPhoto(base64Image);
            }
            service.save(psychologist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editPsychologist(@PathVariable Long id, Model model) {
        Psychologist psychologist = service.findById(id);
        if (psychologist != null) {
            model.addAttribute("psychologists", service.getAll());
            model.addAttribute("psychologist", psychologist);
            return "admin";
        }
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deletePsychologist(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/admin";
    }
}